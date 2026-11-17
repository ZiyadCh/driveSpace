package ma.youcode.driveSpace.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.youcode.driveSpace.dto.PassengerRequest;
import ma.youcode.driveSpace.dto.PassengerResponse;
import ma.youcode.driveSpace.entities.Passenger;
import ma.youcode.driveSpace.exception.BusinessRuleException;
import ma.youcode.driveSpace.exception.ResourceNotFoundException;
import ma.youcode.driveSpace.mapper.PassengerMapper;
import ma.youcode.driveSpace.repository.PassengerRepository;
import ma.youcode.driveSpace.service.PassengerService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

  private final PassengerRepository passengerRepository;
  private final PassengerMapper mapper;

  @Override
  public List<PassengerResponse> findAll() {
    return mapper.toResponseList(passengerRepository.findAll());
  }

  @Override
  public PassengerResponse findById(Long id) {
    return mapper.toResponse(findPassenger(id));
  }

  @Override
  @Transactional
  public PassengerResponse create(PassengerRequest request) {
    if (passengerRepository.existsByEmail(request.email())) {
      throw new BusinessRuleException("A passenger with email " + request.email() + " already exists");
    }

    Passenger passenger = mapper.toEntity(request);
    return mapper.toResponse(passengerRepository.save(passenger));
  }

  @Override
  @Transactional
  public PassengerResponse update(Long id, PassengerRequest request) {
    Passenger passenger = findPassenger(id);

    if (passengerRepository.existsByEmailAndIdNot(request.email(), id)) {
      throw new BusinessRuleException("A passenger with email " + request.email() + " already exists");
    }

    mapper.update(request, passenger);
    return mapper.toResponse(passengerRepository.save(passenger));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Passenger passenger = findPassenger(id);
    if (passengerRepository.hasReservations(id)) {
      throw new BusinessRuleException("Cannot delete passenger " + id + ": they have reservations");
    }
    passengerRepository.delete(passenger);
  }

  private Passenger findPassenger(Long id) {
    return passengerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Passenger " + id + " not found"));
  }
}

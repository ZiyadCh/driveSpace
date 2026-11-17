package ma.youcode.driveSpace.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.youcode.driveSpace.dto.TripRequest;
import ma.youcode.driveSpace.dto.TripResponse;
import ma.youcode.driveSpace.entities.Trip;
import ma.youcode.driveSpace.exception.ResourceNotFoundException;
import ma.youcode.driveSpace.mapper.TripMapper;
import ma.youcode.driveSpace.repository.TripRepository;
import ma.youcode.driveSpace.service.TripService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

  private final TripRepository tripRepository;
  private final TripMapper mapper;

  @Override
  public List<TripResponse> findAll() {
    return mapper.toResponseList(tripRepository.findAll());
  }

  @Override
  public TripResponse findById(Long id) {
    return mapper.toResponse(findTrip(id));
  }

  private Trip findTrip(Long id) {
    return tripRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Trip " + id + " not found"));
  }

  @Override
  public TripResponse create(TripRequest tripRequest) {
    Trip trip = mapper.toEntity(tripRequest);
    return mapper.toResponse(tripRepository.save(trip));
  }

}

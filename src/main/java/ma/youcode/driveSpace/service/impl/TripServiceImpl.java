package ma.youcode.driveSpace.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.youcode.driveSpace.dto.StopPointRequest;
import ma.youcode.driveSpace.dto.TripRequest;
import ma.youcode.driveSpace.dto.TripResponse;
import ma.youcode.driveSpace.entities.Conductor;
import ma.youcode.driveSpace.entities.StopPoint;
import ma.youcode.driveSpace.entities.Trip;
import ma.youcode.driveSpace.entities.Vehicle;
import ma.youcode.driveSpace.exception.BusinessRuleException;
import ma.youcode.driveSpace.exception.ResourceNotFoundException;
import ma.youcode.driveSpace.mapper.StopPointMapper;
import ma.youcode.driveSpace.mapper.TripMapper;
import ma.youcode.driveSpace.repository.ConductorRepository;
import ma.youcode.driveSpace.repository.ReservationRepository;
import ma.youcode.driveSpace.repository.StopPointRepository;
import ma.youcode.driveSpace.repository.TripRepository;
import ma.youcode.driveSpace.repository.VehicleRepository;
import ma.youcode.driveSpace.service.TripService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

  private final TripRepository tripRepository;
  private final StopPointRepository stopPointRepository;
  private final ConductorRepository conductorRepository;
  private final VehicleRepository vehicleRepository;
  private final ReservationRepository reservationRepository;
  private final TripMapper mapper;
  private final StopPointMapper stopPointMapper;

  @Override
  public List<TripResponse> findAll() {
    return mapper.toResponseList(tripRepository.findAll());
  }

  @Override
  public TripResponse findById(Long id) {
    return mapper.toResponse(findTrip(id));
  }

  @Override
  @Transactional
  public TripResponse create(TripRequest tripRequest) {
    Trip trip = mapper.toEntity(tripRequest);
    trip.setConductor(findConductor(tripRequest.conductorId()));
    trip.setVehicle(findVehicle(tripRequest.vehicleId()));

    Trip saved = tripRepository.saveAndFlush(trip);

    for (StopPointRequest sPointRequest : tripRequest.stopPoints()) {
      StopPoint stopPoint = stopPointMapper.toEntity(sPointRequest);
      stopPoint.setTrip(saved);
      saved.getStopPoints().add(stopPointRepository.save(stopPoint));
    }

    return mapper.toResponse(saved);
  }

  @Override
  public Page<TripResponse> search(String villeDepart, String villeArrive, LocalDateTime date, Pageable pageable) {
    LocalDateTime from = null;
    LocalDateTime to = null;
    if (date != null) {
      from = date.toLocalDate().atStartOfDay();
      to = from.plusDays(1);
    }

    return tripRepository.search(
        villeDepart == null || villeDepart.isBlank() ? null : villeDepart,
        villeArrive == null || villeArrive.isBlank() ? null : villeArrive,
        from, to, pageable)
        .map(mapper::toResponse);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Trip trip = findTrip(id);
    if (reservationRepository.existsByTripId(id)) {
      throw new BusinessRuleException("Cannot delete trip " + id + ": it has reservations");
    }
    tripRepository.delete(trip);
  }

  private Trip findTrip(Long id) {
    return tripRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Trip " + id + " not found"));
  }

  private Conductor findConductor(Long id) {
    return conductorRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Conductor " + id + " not found"));
  }

  private Vehicle findVehicle(Long id) {
    return vehicleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Vehicle " + id + " not found"));
  }

}

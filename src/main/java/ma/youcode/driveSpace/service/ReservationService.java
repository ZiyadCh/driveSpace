package ma.youcode.driveSpace.service;

import java.util.List;

import ma.youcode.driveSpace.dto.ReservationRequest;
import ma.youcode.driveSpace.dto.ReservationResponse;

public interface ReservationService {

  ReservationResponse create(ReservationRequest request);

  ReservationResponse findById(Long id);

  List<ReservationResponse> findByPassenger(Long passengerId);

  List<ReservationResponse> findByTripForConductor(Long tripId, Long conductorId);

  void cancel(Long reservationId, Long passengerId);
}

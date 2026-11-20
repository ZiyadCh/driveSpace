package ma.youcode.driveSpace.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ma.youcode.driveSpace.dto.ReservationRequest;
import ma.youcode.driveSpace.dto.ReservationResponse;
import ma.youcode.driveSpace.entities.Reservation;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "passenger", ignore = true)
  @Mapping(target = "trip", ignore = true)
  @Mapping(target = "pickupPoint", ignore = true)
  @Mapping(target = "dropoffPoint", ignore = true)
  @Mapping(target = "totalAmount", ignore = true)
  @Mapping(target = "reservedAt", ignore = true)
  @Mapping(target = "status", ignore = true)
  Reservation toEntity(ReservationRequest request);

  @Mapping(source = "passenger.id", target = "passengerId")
  @Mapping(source = "trip.id", target = "tripId")
  @Mapping(source = "pickupPoint.id", target = "pickupStopPointId")
  @Mapping(source = "pickupPoint.city", target = "pickupCity")
  @Mapping(source = "pickupPoint.stopOrder", target = "pickupOrder")
  @Mapping(source = "dropoffPoint.id", target = "dropoffStopPointId")
  @Mapping(source = "dropoffPoint.city", target = "dropoffCity")
  @Mapping(source = "dropoffPoint.stopOrder", target = "dropoffOrder")
  ReservationResponse toResponse(Reservation reservation);

  List<ReservationResponse> toResponseList(List<Reservation> reservations);
}

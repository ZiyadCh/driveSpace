package ma.youcode.driveSpace.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ma.youcode.driveSpace.dto.StopPointResponse;
import ma.youcode.driveSpace.dto.TripResponse;
import ma.youcode.driveSpace.entities.StopPoint;
import ma.youcode.driveSpace.entities.Trip;

@Mapper(componentModel = "spring")
public interface TripMapper {

  @Mapping(source = "conductor.id", target = "conductorId")
  @Mapping(source = "vehicle.id", target = "vehicleId")
  TripResponse toResponse(Trip trip);

  List<TripResponse> toResponseList(List<Trip> trips);

  StopPointResponse toStopPointResponse(StopPoint stopPoint);
}

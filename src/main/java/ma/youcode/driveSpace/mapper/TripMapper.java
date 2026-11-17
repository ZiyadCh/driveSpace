package ma.youcode.driveSpace.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ma.youcode.driveSpace.dto.StopPointResponse;
import ma.youcode.driveSpace.dto.TripRequest;
import ma.youcode.driveSpace.dto.TripResponse;
import ma.youcode.driveSpace.entities.StopPoint;
import ma.youcode.driveSpace.entities.Trip;

@Mapper(componentModel = "spring")
public interface TripMapper {

  @Mapping(target = "id", ignore = true)
  Trip toEntity(TripRequest request);

  @Mapping(source = "conductor.id", target = "conductorId")
  @Mapping(source = "vehicle.id", target = "vehicleId")
  TripResponse toResponse(Trip trip);

  List<TripResponse> toResponseList(List<Trip> trips);

  StopPointResponse toStopPointResponse(StopPoint stopPoint);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "conductor", ignore = true)
  @Mapping(target = "vehicle", ignore = true)
  @Mapping(target = "stopPoints", ignore = true)
  void update(TripRequest request, @MappingTarget Trip trip);
}

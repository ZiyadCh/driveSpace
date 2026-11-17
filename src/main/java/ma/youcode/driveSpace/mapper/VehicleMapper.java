package ma.youcode.driveSpace.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import ma.youcode.driveSpace.dto.VehicleRequest;
import ma.youcode.driveSpace.dto.VehicleResponse;
import ma.youcode.driveSpace.entities.Vehicle;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "conductor", ignore = true)
  Vehicle toEntity(VehicleRequest request);

  @Mapping(source = "conductor.id", target = "conductorId")
  VehicleResponse toResponse(Vehicle vehicle);

  List<VehicleResponse> toResponseList(List<Vehicle> vehicles);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "conductor", ignore = true)
  void update(VehicleRequest request, @MappingTarget Vehicle vehicle);
}

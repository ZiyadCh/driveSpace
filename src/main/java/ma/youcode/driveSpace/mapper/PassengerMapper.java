package ma.youcode.driveSpace.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ma.youcode.driveSpace.dto.PassengerRequest;
import ma.youcode.driveSpace.dto.PassengerResponse;
import ma.youcode.driveSpace.entities.Passenger;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

  @Mapping(target = "id", ignore = true)
  Passenger toEntity(PassengerRequest request);

  PassengerResponse toResponse(Passenger passenger);

  List<PassengerResponse> toResponseList(List<Passenger> passengers);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  void update(PassengerRequest request, @MappingTarget Passenger passenger);
}

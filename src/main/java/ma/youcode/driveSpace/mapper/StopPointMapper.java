package ma.youcode.driveSpace.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ma.youcode.driveSpace.dto.StopPointRequest;
import ma.youcode.driveSpace.entities.StopPoint;

@Mapper(componentModel = "spring")
public interface StopPointMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "trip", ignore = true)
  StopPoint toEntity(StopPointRequest request);
}

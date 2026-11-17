package ma.youcode.driveSpace.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ma.youcode.driveSpace.dto.ConductorRequest;
import ma.youcode.driveSpace.dto.ConductorResponse;
import ma.youcode.driveSpace.entities.Conductor;

@Mapper(componentModel = "spring")
public interface ConductorMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "vehicles", ignore = true)
  Conductor toEntity(ConductorRequest request);

  ConductorResponse toResponse(Conductor conductor);

  List<ConductorResponse> toResponseList(List<Conductor> conductors);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "vehicles", ignore = true)
  void update(ConductorRequest request, @MappingTarget Conductor conductor);
}

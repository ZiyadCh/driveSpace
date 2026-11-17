package ma.youcode.driveSpace.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.youcode.driveSpace.dto.ConductorRequest;
import ma.youcode.driveSpace.dto.ConductorResponse;
import ma.youcode.driveSpace.entities.Conductor;
import ma.youcode.driveSpace.exception.BusinessRuleException;
import ma.youcode.driveSpace.exception.ResourceNotFoundException;
import ma.youcode.driveSpace.mapper.ConductorMapper;
import ma.youcode.driveSpace.repository.ConductorRepository;
import ma.youcode.driveSpace.repository.VehicleRepository;
import ma.youcode.driveSpace.service.ConductorService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConductorServiceImpl implements ConductorService {

  private final ConductorRepository conductorRepository;
  private final VehicleRepository vehicleRepository;
  private final ConductorMapper mapper;

  @Override
  public List<ConductorResponse> findAll() {
    return mapper.toResponseList(conductorRepository.findAll());
  }

  @Override
  public ConductorResponse findById(Long id) {
    return mapper.toResponse(findConductor(id));
  }

  @Override
  @Transactional
  public ConductorResponse create(ConductorRequest request) {
    if (conductorRepository.existsByEmail(request.email())) {
      throw new BusinessRuleException("A conductor with email " + request.email() + " already exists");
    }
    if (conductorRepository.existsByLicenseNumber(request.licenseNumber())) {
      throw new BusinessRuleException(
          "A conductor with license number " + request.licenseNumber() + " already exists");
    }

    Conductor conductor = mapper.toEntity(request);
    return mapper.toResponse(conductorRepository.save(conductor));
  }

  @Override
  @Transactional
  public ConductorResponse update(Long id, ConductorRequest request) {
    Conductor conductor = findConductor(id);

    if (conductorRepository.existsByEmailAndIdNot(request.email(), id)) {
      throw new BusinessRuleException("A conductor with email " + request.email() + " already exists");
    }
    if (conductorRepository.existsByLicenseNumberAndIdNot(request.licenseNumber(), id)) {
      throw new BusinessRuleException(
          "A conductor with license number " + request.licenseNumber() + " already exists");
    }

    mapper.update(request, conductor);
    return mapper.toResponse(conductorRepository.save(conductor));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Conductor conductor = findConductor(id);
    if (vehicleRepository.existsByConductorId(id)) {
      throw new BusinessRuleException("Cannot delete conductor " + id + ": they still own vehicles");
    }
    conductorRepository.delete(conductor);
  }

  private Conductor findConductor(Long id) {
    return conductorRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Conductor " + id + " not found"));
  }
}

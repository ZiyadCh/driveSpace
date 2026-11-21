package ma.youcode.driveSpace.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.youcode.driveSpace.dto.VehicleRequest;
import ma.youcode.driveSpace.dto.VehicleResponse;
import ma.youcode.driveSpace.entities.Conductor;
import ma.youcode.driveSpace.entities.Vehicle;
import ma.youcode.driveSpace.exception.BusinessRuleException;
import ma.youcode.driveSpace.exception.ForbiddenActionException;
import ma.youcode.driveSpace.exception.ResourceNotFoundException;
import ma.youcode.driveSpace.mapper.VehicleMapper;
import ma.youcode.driveSpace.repository.ConductorRepository;
import ma.youcode.driveSpace.repository.VehicleRepository;
import ma.youcode.driveSpace.service.VehicleService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

  private final VehicleRepository vehicleRepository;
  private final ConductorRepository conductorRepository;
  private final VehicleMapper mapper;

  @Override
  public List<VehicleResponse> findAll(Long conductorId) {
    return mapper.toResponseList(vehicleRepository.findByConductorId(conductorId));
  }

  @Override
  public VehicleResponse findById(Long id, Long conductorId) {
    return mapper.toResponse(findOwnedVehicle(id, conductorId));
  }

  @Override
  @Transactional
  public VehicleResponse create(VehicleRequest request, Long conductorId) {
    if (vehicleRepository.existsByPlateNumber(request.plateNumber())) {
      throw new BusinessRuleException("A vehicle with plate number " + request.plateNumber() + " already exists");
    }

    Vehicle vehicle = mapper.toEntity(request);
    vehicle.setConductor(findConductor(conductorId));
    return mapper.toResponse(vehicleRepository.save(vehicle));
  }

  @Override
  @Transactional
  public VehicleResponse update(Long id, VehicleRequest request, Long conductorId) {
    Vehicle vehicle = findOwnedVehicle(id, conductorId);

    if (vehicleRepository.existsByPlateNumberAndIdNot(request.plateNumber(), id)) {
      throw new BusinessRuleException("A vehicle with plate number " + request.plateNumber() + " already exists");
    }

    mapper.update(request, vehicle);
    vehicle.setConductor(findConductor(conductorId));
    return mapper.toResponse(vehicleRepository.save(vehicle));
  }

  @Override
  @Transactional
  public void delete(Long id, Long conductorId) {
    Vehicle vehicle = findOwnedVehicle(id, conductorId);
    if (vehicleRepository.isUsedInTrips(id)) {
      throw new BusinessRuleException("Cannot delete vehicle " + id + ": it is used by a trip");
    }
    vehicleRepository.delete(vehicle);
  }

  private Vehicle findOwnedVehicle(Long id, Long conductorId) {
    Vehicle vehicle = findVehicle(id);
    if (!vehicle.getConductor().getId().equals(conductorId)) {
      throw new ForbiddenActionException("You can only manage your own vehicles.");
    }
    return vehicle;
  }

  private Vehicle findVehicle(Long id) {
    return vehicleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Vehicle " + id + " not found"));
  }

  private Conductor findConductor(Long id) {
    return conductorRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Conductor " + id + " not found"));
  }
}

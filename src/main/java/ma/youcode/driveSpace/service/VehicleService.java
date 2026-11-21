package ma.youcode.driveSpace.service;

import java.util.List;

import ma.youcode.driveSpace.dto.VehicleRequest;
import ma.youcode.driveSpace.dto.VehicleResponse;

public interface VehicleService {

  List<VehicleResponse> findAll(Long conductorId);

  VehicleResponse findById(Long id, Long conductorId);

  VehicleResponse create(VehicleRequest request, Long conductorId);

  VehicleResponse update(Long id, VehicleRequest request, Long conductorId);

  void delete(Long id, Long conductorId);
}

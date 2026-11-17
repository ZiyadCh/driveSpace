package ma.youcode.driveSpace.service;

import java.util.List;

import ma.youcode.driveSpace.dto.VehicleRequest;
import ma.youcode.driveSpace.dto.VehicleResponse;

public interface VehicleService {

  List<VehicleResponse> findAll();

  VehicleResponse findById(Long id);

  VehicleResponse create(VehicleRequest request);

  VehicleResponse update(Long id, VehicleRequest request);

  void delete(Long id);
}

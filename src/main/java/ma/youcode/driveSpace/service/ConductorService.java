package ma.youcode.driveSpace.service;

import java.util.List;

import ma.youcode.driveSpace.dto.ConductorRequest;
import ma.youcode.driveSpace.dto.ConductorResponse;

public interface ConductorService {

  List<ConductorResponse> findAll();

  ConductorResponse findById(Long id);

  ConductorResponse create(ConductorRequest request);

  ConductorResponse update(Long id, ConductorRequest request);

  void delete(Long id);
}

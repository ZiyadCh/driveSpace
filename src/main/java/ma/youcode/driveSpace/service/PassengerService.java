package ma.youcode.driveSpace.service;

import java.util.List;

import ma.youcode.driveSpace.dto.PassengerRequest;
import ma.youcode.driveSpace.dto.PassengerResponse;

public interface PassengerService {

  List<PassengerResponse> findAll();

  PassengerResponse findById(Long id);

  PassengerResponse create(PassengerRequest request);

  PassengerResponse update(Long id, PassengerRequest request);

  void delete(Long id);
}

package ma.youcode.driveSpace.service;

import java.util.List;

import ma.youcode.driveSpace.dto.TripRequest;
import ma.youcode.driveSpace.dto.TripResponse;

public interface TripService {

  List<TripResponse> findAll();

  TripResponse findById(Long id);

  TripResponse create(TripRequest tripRequest);

}

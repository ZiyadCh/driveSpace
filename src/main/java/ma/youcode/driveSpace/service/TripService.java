package ma.youcode.driveSpace.service;

import java.util.List;

import org.springframework.data.domain.Page;

import ma.youcode.driveSpace.dto.TripRequest;
import ma.youcode.driveSpace.dto.TripResponse;

public interface TripService {

  List<TripResponse> findAll();

  Page<TripResponse> search();

  TripResponse findById(Long id);

  TripResponse create(TripRequest tripRequest);

  void delete(Long id);
}

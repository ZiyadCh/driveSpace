package ma.youcode.driveSpace.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ma.youcode.driveSpace.dto.TripRequest;
import ma.youcode.driveSpace.dto.TripResponse;

public interface TripService {

  List<TripResponse> findAll();

  Page<TripResponse> search(String villeDepart, String villeArrive, LocalDateTime date, Pageable pageable);

  TripResponse findById(Long id);

  TripResponse create(TripRequest tripRequest);

  void delete(Long id);
}

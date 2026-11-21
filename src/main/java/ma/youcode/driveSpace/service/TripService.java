package ma.youcode.driveSpace.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ma.youcode.driveSpace.dto.TripRequest;
import ma.youcode.driveSpace.dto.TripResponse;
import ma.youcode.driveSpace.dto.TripSearchResponse;

public interface TripService {

  List<TripResponse> findAll();

  Page<TripResponse> search(String villeDepart, String villeArrive, LocalDateTime date, Pageable pageable);

  Page<TripSearchResponse> searchByStops(String fromCity, String toCity, LocalDateTime dayStart,
      long seats, BigDecimal maxPrice, Pageable pageable);

  TripResponse findById(Long id);

  TripResponse create(TripRequest tripRequest);

  void delete(Long id);

  void cancel(Long tripId, Long conductorId);
}

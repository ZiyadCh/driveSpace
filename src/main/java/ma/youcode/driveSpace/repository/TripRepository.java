package ma.youcode.driveSpace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.youcode.driveSpace.entities.Trip;
import java.time.LocalDateTime;

public interface TripRepository extends JpaRepository<Trip, Long> {
  List<Trip> findByDepartureCity(String departureCity);

  List<Trip> findByArrivalCity(String arrivalCity);

  List<Trip> findByDepartureTime(LocalDateTime departureTime);
}

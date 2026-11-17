package ma.youcode.driveSpace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.youcode.driveSpace.entities.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  boolean existsByTripId(Long tripId);
}

package ma.youcode.driveSpace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.youcode.driveSpace.entities.Reservation;
import ma.youcode.driveSpace.enums.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  boolean existsByTripId(Long tripId);

  boolean existsByPassengerIdAndTripIdAndStatus(Long passengerId, Long tripId, ReservationStatus status);

  List<Reservation> findByPassengerIdOrderByReservedAtDesc(Long passengerId);

  List<Reservation> findByTripIdOrderByReservedAtDesc(Long tripId);

  @Query("""
      select coalesce(sum(r.seats), 0)
      from Reservation r
      where r.trip.id = :tripId
        and r.status = :status
      """)
  int sumSeatsByTripAndStatus(@Param("tripId") Long tripId, @Param("status") ReservationStatus status);
}

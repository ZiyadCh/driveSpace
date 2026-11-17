package ma.youcode.driveSpace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.youcode.driveSpace.entities.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  @Query(value = "select exists(select 1 from reservations where passenger_id = :id)", nativeQuery = true)
  boolean hasReservations(@Param("id") Long id);
}

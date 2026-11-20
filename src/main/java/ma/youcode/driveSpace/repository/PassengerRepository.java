package ma.youcode.driveSpace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.youcode.driveSpace.entities.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  @Query(value = "select exists(select 1 from reservations where passenger_id = :id)", nativeQuery = true)
  boolean hasReservations(@Param("id") Long id);

  @Query("""
      select p from Passenger p
      where (cast(:name as string) is null
             or lower(p.firstName) like lower(concat('%', cast(:name as string), '%'))
             or lower(p.lastName) like lower(concat('%', cast(:name as string), '%')))
        and (cast(:email as string) is null
             or lower(p.email) like lower(concat('%', cast(:email as string), '%')))
      """)
  List<Passenger> search(@Param("name") String name, @Param("email") String email);
}

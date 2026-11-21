package ma.youcode.driveSpace.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.youcode.driveSpace.entities.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {

  List<Trip> findByDepartureCity(String departureCity);

  List<Trip> findByArrivalCity(String arrivalCity);

  List<Trip> findByDepartureTime(LocalDateTime departureTime);

  @Query("""
      select t from Trip t
      where (cast(:villeDepart as string) is null or t.departureCity = :villeDepart)
        and (cast(:villeArrive as string) is null or t.arrivalCity = :villeArrive)
        and (cast(:from as timestamp) is null or t.departureTime >= :from)
        and (cast(:to as timestamp) is null or t.departureTime < :to)
      """)
  Page<Trip> search(@Param("villeDepart") String villeDepart,
      @Param("villeArrive") String villeArrive,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      Pageable pageable);

  @Query("""
      select new ma.youcode.driveSpace.dto.TripSearchResponse(
       t.id, t.departureCity, t.arrivalCity, t.departureTime
       ,t.pickupCity,t.dropoffCity,
       (dep.segmentPrice - arr.segmentPrice),
        t.offeredSeats - coalesce((select sum(r.seats) from Reservation r
            where r.trip = tri and r.status = 'CONFIRME'),0)
             from Trip t
             join StopPoint dep
             join StopPoint arr
             join Reservation r
             """)
  Page<Trip> search(@Param("from") String from, @Param("to") String to,
      @Param("dayStart") LocalDateTime dayStart,
      @Param("dayEnd") LocalDateTime dayEnd,
      @Param("seats") long seats,
      @Param("maxPrice") BigDecimal maxPrice,
      Pageable pageable);
}

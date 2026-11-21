package ma.youcode.driveSpace.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.youcode.driveSpace.dto.TripSearchResponse;
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

  @Query(value = """
      select new ma.youcode.driveSpace.dto.TripSearchResponse(
       t.id, t.departureCity, t.arrivalCity, t.departureTime
       ,dep.city,arr.city,
       (arr.priceFromDeparture - dep.priceFromDeparture),
        t.offeredSeats - coalesce((select sum(r.seats) from Reservation r
            where r.trip = t and r.status = 'CONFIRMEE'),0)
        )
             from Trip t
             join t.stopPoints dep
             join t.stopPoints arr
             where dep.city = :fromCity
             and arr.city = :toCity
             and dep.stopOrder < arr.stopOrder
             """, countQuery = """
      select count(t)
      from Trip t
      join t.stopPoints dep
      join t.stopPoints arr
      where dep.city = :fromCity
        and arr.city = :toCity
        and dep.stopOrder < arr.stopOrder
      """)
  Page<TripSearchResponse> search(@Param("fromCity") String fromCity,
      @Param("toCity") String toCity,
      @Param("dayStart") LocalDateTime dayStart,
      @Param("seats") long seats,
      @Param("maxPrice") BigDecimal maxPrice,
      Pageable pageable);
}

package ma.youcode.driveSpace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.youcode.driveSpace.entities.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

  boolean existsByPlateNumber(String plateNumber);

  boolean existsByPlateNumberAndIdNot(String plateNumber, Long id);

  boolean existsByConductorId(Long conductorId);

  @Query(value = "select exists(select 1 from trips where vehicle_id = :id)", nativeQuery = true)
  boolean isUsedInTrips(@Param("id") Long id);
}

package ma.youcode.driveSpace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.youcode.driveSpace.entities.Vehicle;

/**
 * VehicleRepository
 */
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}

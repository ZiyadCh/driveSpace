package ma.youcode.driveSpace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.youcode.driveSpace.entities.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
}

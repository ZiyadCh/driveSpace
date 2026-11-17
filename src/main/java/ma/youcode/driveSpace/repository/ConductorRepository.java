package ma.youcode.driveSpace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.youcode.driveSpace.entities.Conductor;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  boolean existsByLicenseNumber(String licenseNumber);

  boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);
}

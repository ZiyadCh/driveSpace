package ma.youcode.driveSpace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.youcode.driveSpace.entities.Conductor;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  boolean existsByLicenseNumber(String licenseNumber);

  boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);

  @Query("""
      select c from Conductor c
      where (cast(:name as string) is null
             or lower(c.firstName) like lower(concat('%', cast(:name as string), '%'))
             or lower(c.lastName) like lower(concat('%', cast(:name as string), '%')))
        and (cast(:email as string) is null
             or lower(c.email) like lower(concat('%', cast(:email as string), '%')))
      """)
  List<Conductor> search(@Param("name") String name, @Param("email") String email);
}

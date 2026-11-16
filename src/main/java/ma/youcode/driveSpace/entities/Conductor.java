package ma.youcode.driveSpace.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conductors")
@Getter
@Setter
@NoArgsConstructor
public class Conductor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false, unique = true)
  private String licenseNumber;

  private LocalDate licenseExpiry;

  private String phone;

  @OneToMany(mappedBy = "conductor", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Vehicle> vehicles = new ArrayList<>();

  public void addVehicle(Vehicle vehicle) {
    vehicles.add(vehicle);
    vehicle.setConductor(this);
  }

  public void removeVehicle(Vehicle vehicle) {
    vehicles.remove(vehicle);
    vehicle.setConductor(null);
  }
}

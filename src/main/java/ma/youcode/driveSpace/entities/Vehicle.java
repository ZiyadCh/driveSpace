package ma.youcode.driveSpace.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
public class Vehicle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "plate_number", nullable = false, unique = true, length = 20)
  private String plateNumber;

  @Column(nullable = false, length = 50)
  private String brand;

  @Column(nullable = false, length = 50)
  private String model;

  @Column(length = 30)
  private String color;

  @Min(1)
  @Max(8)
  @Column(nullable = false)
  private int capacity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "conductor_id", nullable = false)
  private Conductor conductor;
}

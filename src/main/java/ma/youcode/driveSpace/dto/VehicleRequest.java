package ma.youcode.driveSpace.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record VehicleRequest(

    @NotBlank(message = "Plate number is required") @Size(max = 20, message = "Plate number must not exceed 20 characters") String plateNumber,

    @NotBlank(message = "Brand is required") @Size(max = 50, message = "Brand must not exceed 50 characters") String brand,

    @NotBlank(message = "Model is required") @Size(max = 50, message = "Model must not exceed 50 characters") String model,

    @Size(max = 30, message = "Color must not exceed 30 characters") String color,

    @Min(value = 1, message = "Capacity must be at least 1") @Max(value = 8, message = "Capacity must not exceed 8") int capacity,

    @NotNull(message = "Conductor id is required") @Positive(message = "Conductor id must be positive") Long conductorId) {
}

package ma.youcode.driveSpace.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ma.youcode.driveSpace.enums.TripStatus;

public record TripRequest(

    @NotNull(message = "Conductor id is required") @Positive(message = "Conductor id must be positive") Long conductorId,

    @NotNull(message = "Vehicle id is required") @Positive(message = "Vehicle id must be positive") Long vehicleId,

    @NotBlank(message = "Departure city is required") @Size(max = 100, message = "Departure city must not exceed 100 characters") String departureCity,

    @NotBlank(message = "Arrival city is required") @Size(max = 100, message = "Arrival city must not exceed 100 characters") String arrivalCity,

    @NotNull(message = "Departure time is required") LocalDateTime departureTime,

    @Min(value = 1, message = "Offered seats must be between 1 and 8") @Max(value = 8, message = "Offered seats must be between 1 and 8") int offeredSeats,

    @NotNull(message = "Status is required") TripStatus status,

    @Valid @NotNull(message = "Stop points are required") List<StopPointResponse> stopPoints

) {
}

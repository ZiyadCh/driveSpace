package ma.youcode.driveSpace.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationRequest(

    @NotNull(message = "Passenger id is required")
    @Positive(message = "Passenger id must be positive")
    Long passengerId,

    @NotNull(message = "Trip id is required")
    @Positive(message = "Trip id must be positive")
    Long tripId,

    @NotBlank(message = "Pick-up city is required")
    String pickupCity,

    @NotBlank(message = "Drop-off city is required")
    String dropoffCity,

    @Min(value = 1, message = "At least one seat must be reserved")
    int seats
) {
}

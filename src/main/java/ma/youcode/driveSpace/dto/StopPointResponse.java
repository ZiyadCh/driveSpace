package ma.youcode.driveSpace.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StopPointResponse(

    @NotNull(message = "Id is required")
    @Positive(message = "Id must be positive")
    Long id,

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    String city,

    @Positive(message = "Stop order must be positive")
    int stopOrder,

    @NotNull(message = "Passage time is required")
    LocalTime passageTime,

    @NotNull(message = "Price from departure is required")
    @DecimalMin(value = "0.00", message = "Price from departure must not be negative")
    BigDecimal priceFromDeparture
) {
}

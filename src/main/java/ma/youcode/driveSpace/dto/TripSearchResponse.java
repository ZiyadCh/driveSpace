package ma.youcode.driveSpace.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripSearchResponse(

    Long tripId,
    String departureCity,
    String arrivalCity,
    LocalDateTime departureTime,

    String pickupCity,
    String dropoffCity,

    BigDecimal segmentPrice,
    int remainingSeats

) {
}

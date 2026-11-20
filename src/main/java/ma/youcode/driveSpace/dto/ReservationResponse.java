package ma.youcode.driveSpace.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import ma.youcode.driveSpace.enums.ReservationStatus;

public record ReservationResponse(

    Long id,
    Long passengerId,
    Long tripId,

    Long pickupStopPointId,
    String pickupCity,
    int pickupOrder,

    Long dropoffStopPointId,
    String dropoffCity,
    int dropoffOrder,

    int seats,
    BigDecimal totalAmount,
    LocalDateTime reservedAt,
    ReservationStatus status
) {
}

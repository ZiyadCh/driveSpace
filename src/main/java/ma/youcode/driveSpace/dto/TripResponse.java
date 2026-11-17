package ma.youcode.driveSpace.dto;

import java.time.LocalDateTime;
import java.util.List;

import ma.youcode.driveSpace.enums.TripStatus;

public record TripResponse(
    Long id,
    Long conductorId,
    Long vehicleId,
    String departureCity,
    String arrivalCity,
    LocalDateTime departureTime,
    int offeredSeats,
    TripStatus status,
    List<StopPointResponse> stopPoints
) {
}

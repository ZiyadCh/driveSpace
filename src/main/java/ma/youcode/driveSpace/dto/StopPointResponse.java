package ma.youcode.driveSpace.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

public record StopPointResponse(
    Long id,
    String city,
    int stopOrder,
    LocalTime passageTime,
    BigDecimal priceFromDeparture
) {
}

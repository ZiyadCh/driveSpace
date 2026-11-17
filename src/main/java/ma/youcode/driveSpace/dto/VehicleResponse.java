package ma.youcode.driveSpace.dto;

public record VehicleResponse(
    Long id,
    String plateNumber,
    String brand,
    String model,
    String color,
    int capacity,
    Long conductorId
) {
}

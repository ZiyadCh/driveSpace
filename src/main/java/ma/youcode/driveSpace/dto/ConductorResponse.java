package ma.youcode.driveSpace.dto;

public record ConductorResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String licenseNumber
) {
}

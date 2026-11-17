package ma.youcode.driveSpace.dto;

public record PassengerResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone
) {
}

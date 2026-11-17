package ma.youcode.driveSpace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConductorRequest(

    @NotBlank(message = "First name is required") @Size(max = 100, message = "First name must not exceed 100 characters") String firstName,

    @NotBlank(message = "Last name is required") @Size(max = 100, message = "Last name must not exceed 100 characters") String lastName,

    @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 150, message = "Email must not exceed 150 characters") String email,

    @Size(max = 20, message = "Phone must not exceed 20 characters") String phone,

    @NotBlank(message = "License number is required") @Size(max = 50, message = "License number must not exceed 50 characters") String licenseNumber) {
}

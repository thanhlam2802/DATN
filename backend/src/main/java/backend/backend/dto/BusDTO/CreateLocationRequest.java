package backend.backend.dto.BusDTO;


    import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

    public record CreateLocationRequest(
            @NotNull(message = "Location name must not be null")
            @Size(max = 255, message = "Location name must not exceed 255 characters")
            String name,

            @Size(max = 255, message = "Address must not exceed 255 characters")
            String address,

            @Size(max = 100, message = "City must not exceed 100 characters")
            String city,

            @Size(max = 100, message = "District must not exceed 100 characters")
            String district,

            @Size(max = 100, message = "Province must not exceed 100 characters")
            String province,

            @Size(max = 100, message = "Country must not exceed 100 characters")
            String country,

            @Size(max = 20, message = "Postal code must not exceed 20 characters")
            String postalCode


    ) {}


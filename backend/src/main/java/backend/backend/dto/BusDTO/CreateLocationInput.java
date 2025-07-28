package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record
CreateLocationInput(
        @NotNull(message = "Location name must not be null")
        @Size(max = 200, message = "Location name must not exceed 200 characters")
        String name,

        @Size(max = 200, message = "Province/City must not exceed 200 characters")
        String provinceCity,

        @Size(max = 200, message = "District must not exceed 200 characters")
        String district,

        @Size(max = 500, message = "Address details must not exceed 500 characters")
        String addressDetails

        ) {
}

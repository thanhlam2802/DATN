package backend.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GenderEnum {
    MALE,
    FEMALE,
    @JsonEnumDefaultValue
    UNKNOWN;

    @JsonCreator
    public static GenderEnum fromValue(String value) {
        try {
            return GenderEnum.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
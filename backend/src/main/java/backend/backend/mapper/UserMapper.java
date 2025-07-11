package backend.backend.mapper;


import backend.backend.dto.AccountDto;
import backend.backend.dto.auth.RegisterRequestDto;
import backend.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
   AccountDto fromEntityToProfile(User user);

   User fromRegisterRequestToEntity(RegisterRequestDto registerRequestDto);
}

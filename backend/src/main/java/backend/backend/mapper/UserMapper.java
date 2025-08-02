package backend.backend.mapper;


import backend.backend.dto.AccountDto;
import backend.backend.dto.auth.RegisterRequestDto;
import backend.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
   @Mapping(target = "roles", expression = "java(mapRoles(user))")
   AccountDto fromEntityToProfile(User user);

   User fromRegisterRequestToEntity(RegisterRequestDto registerRequestDto);
   
   default List<String> mapRoles(User user) {
       if (user.getUserRoles() == null) {
           return List.of();
       }
       return user.getUserRoles().stream()
           .map(userRole -> userRole.getRole().getName().name())
           .collect(Collectors.toList());
   }
}

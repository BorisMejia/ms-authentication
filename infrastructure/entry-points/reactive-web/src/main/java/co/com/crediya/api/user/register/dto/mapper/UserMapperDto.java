package co.com.crediya.api.user.register.dto.mapper;

import co.com.crediya.api.user.register.dto.response.CreateUserResponseDto;
import co.com.crediya.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapperDto {
    CreateUserResponseDto toResponse(User user);
}

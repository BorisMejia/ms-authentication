package co.com.crediya.api.user.dto.mapper;

import co.com.crediya.api.user.dto.request.CreateUserRequestDto;
import co.com.crediya.api.user.dto.response.CreateUserResponseDto;
import co.com.crediya.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapperDto {

    User toDomain(CreateUserRequestDto createUserRequestDto);

    CreateUserResponseDto toResponse(User user);
}

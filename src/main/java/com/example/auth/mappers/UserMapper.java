package com.example.auth.mappers;

import com.example.auth.dtos.users.UserRequest;
import com.example.auth.dtos.users.UserResponse;
import com.example.auth.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    User toEntity(UserRequest request);

    UserResponse toResponse(User user);;
}

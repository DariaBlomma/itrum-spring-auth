package com.example.auth.mappers;

import com.example.auth.dtos.auth.SignUpRequest;
import com.example.auth.dtos.users.UserRequest;
import com.example.auth.dtos.users.UserResponse;
import com.example.auth.dtos.users.UserResponseWithPassword;
import com.example.auth.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toEntityWithoutPassword(SignUpRequest request);

    User toEntity(UserRequest request);

    UserResponse toResponse(User user);

    UserResponseWithPassword toResponseWithPassword(User user);;
    
    UserResponse toResponseWithPassword(UserResponseWithPassword user);;
}

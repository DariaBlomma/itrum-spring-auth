package com.example.auth.mappers;

import com.example.auth.dtos.authors.AuthorRequest;
import com.example.auth.dtos.authors.AuthorResponse;
import com.example.auth.entities.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    Author toEntity(AuthorRequest request);

    AuthorResponse toResponse(Author author);;
}

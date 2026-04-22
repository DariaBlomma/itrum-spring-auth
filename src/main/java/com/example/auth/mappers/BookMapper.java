package com.example.auth.mappers;

import com.example.auth.dtos.books.BookRequest;
import com.example.auth.dtos.books.BookResponse;
import com.example.auth.entities.User;
import com.example.auth.entities.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Book toEntity(BookRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "deletedAt", ignore = true)
    BookResponse toResponse(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void update(BookRequest request, @MappingTarget Book book);
}

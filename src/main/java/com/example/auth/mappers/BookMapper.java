package com.example.auth.mappers;

import com.example.auth.dtos.books.BookRequest;
import com.example.auth.dtos.books.BookResponse;
import com.example.auth.entities.User;
import com.example.auth.entities.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {
    Book toEntity(BookRequest request);

    @Mapping(source = "user.id", target = "userId")
    BookResponse toResponse(Book book);

    void update(BookRequest request, @MappingTarget Book book);
}

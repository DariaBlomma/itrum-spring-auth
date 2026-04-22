package com.example.auth.mappers;

import com.example.auth.dtos.books.BookRequest;
import com.example.auth.dtos.books.BookResponse;
import com.example.auth.entities.Author;
import com.example.auth.entities.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book toEntity(BookRequest request);

    @Mapping(source = "authors", target = "authorIds")
    BookResponse toResponse(Book book);

    default Set<Long> mapAuthorsToIds(Collection<Author> authors) {
        if (authors == null) return Set.of();
        return authors.stream()
                .filter(author -> !author.isDeleted())
                .map(Author::getId)
                .collect(Collectors.toUnmodifiableSet());
    }

    void update(BookRequest request, @MappingTarget Book user);
}

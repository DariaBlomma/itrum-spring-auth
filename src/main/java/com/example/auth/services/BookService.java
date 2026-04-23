package com.example.auth.services;

import com.example.auth.dtos.books.BookRequest;
import com.example.auth.dtos.books.BookResponse;
import com.example.auth.entities.User;
import com.example.auth.entities.Book;
import com.example.auth.exceptions.ResourceNotFoundException;
import com.example.auth.mappers.BookMapper;
import com.example.auth.repositories.UserRepository;
import com.example.auth.repositories.BookRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UserRepository userRepository;

    @Transactional
    public BookResponse create(Long userId, BookRequest request) {
        User user = userRepository.findActiveById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found or deleted with id: " + userId));
        Book book = bookMapper.toEntity(request);
        book.setUser(user);
        Book saved = bookRepository.save(book);
        return bookMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BookResponse getOne(Long userId, Long bookId) {
        Book book = bookRepository.findActiveByIdForUser(bookId, userId).orElseThrow(
                () -> new ResourceNotFoundException("Active book does not exist or does not belong to user. Book id: " + bookId));
        return bookMapper.toResponse(book);
    }
}

package com.example.auth.services;

import com.example.auth.dtos.users.UserRequest;
import com.example.auth.dtos.users.UserResponse;
import com.example.auth.entities.User;
import com.example.auth.entities.Book;
import com.example.auth.exceptions.InvalidRequestException;
import com.example.auth.exceptions.ResourceNotFoundException;
import com.example.auth.mappers.UserMapper;
import com.example.auth.repositories.UserRepository;
import com.example.auth.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BookRepository bookRepository;

    @Transactional
    public UserResponse create(UserRequest request) {
        User user = userMapper.toEntity(request);
        Set<Long> requestIds = request.getBookIds();
        if (!requestIds.isEmpty()) {
            List<Book> activeBooks = bookRepository.findActiveByIds(request.getBookIds());
            checkBooksOfRequest(activeBooks, requestIds);
            user.setBooks(new HashSet<>(activeBooks));
        } else {
            user.setBooks(new HashSet<>());
        }
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getOne(Long authorId) {
        User user = userRepository.findActiveById(authorId).orElseThrow(
                () -> new ResourceNotFoundException("Author does not exist or deleted with such id " + authorId)
        );
        return userMapper.toResponse(user);
    }

    private void checkBooksOfRequest(List<Book> activeBooks, Set<Long> requestIds) {
        if (activeBooks.size() < requestIds.size()) {
            Set<Long> foundIds = activeBooks.stream()
                    .map(Book::getId)
                    .collect(Collectors.toSet());

            Set<Long> invalidIds = requestIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());
            throw new InvalidRequestException("Some books do not exist or were deleted. Invalid ids are: " + invalidIds);
        }
    }
}

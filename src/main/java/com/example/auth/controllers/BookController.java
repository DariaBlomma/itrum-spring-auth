package com.example.auth.controllers;

import com.example.auth.dtos.books.BookRequest;
import com.example.auth.dtos.books.BookResponse;
import com.example.auth.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(Long userId, @Valid @RequestBody BookRequest request) {
        return bookService.create(userId, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponse getOne(Long userId, @PathVariable("id") Long bookId) {
        return bookService.getOne(userId, bookId);
    }
}

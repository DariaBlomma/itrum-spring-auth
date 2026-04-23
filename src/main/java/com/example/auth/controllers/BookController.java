package com.example.auth.controllers;

import com.example.auth.dtos.books.BookRequest;
import com.example.auth.dtos.books.BookResponse;
import com.example.auth.security.CustomUserDetails;
import com.example.auth.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @Secured({"ROLE_MODERATOR", "ROLE_SUPER_ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody BookRequest request) {
        return bookService.create(userDetails.getId(), request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponse getOne(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("id") Long bookId) {
        return bookService.getOne(userDetails.getId(), bookId);
    }
}

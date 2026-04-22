package com.example.auth.controllers;

import com.example.auth.dtos.authors.AuthorRequest;
import com.example.auth.dtos.authors.AuthorResponse;
import com.example.auth.services.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@Valid @RequestBody AuthorRequest request) {
        return authorService.create(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorResponse getOne(@PathVariable("id") Long authorId) {
        return authorService.getOne(authorId);
    }
}

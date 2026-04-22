package com.example.auth.services;

import com.example.auth.entities.User;
import com.example.auth.entities.Book;
import com.example.auth.repositories.UserRepository;
import com.example.auth.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import java.time.Instant;
import java.time.Year;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public abstract class BaseServiceTest {
    @Autowired
    protected BookRepository bookRepository;

    @Autowired
    protected UserRepository userRepository;

    protected User saveTestAuthor() {
        User user = User.builder()
                .name("Author One")
                .build();
        return userRepository.save(user);
    }

    protected User saveAnotherTestAuthor() {
        User user = User.builder()
                .name("Author Two")
                .build();
        return userRepository.save(user);
    }

    protected User saveTestAuthor3() {
        User user = User.builder()
                .name("Author Three")
                .build();
        return userRepository.save(user);
    }

    protected User saveDeletedTestAuthor() {
        User user = User.builder()
                .name("Deleted Author")
                .deletedAt(Instant.now())
                .build();
        return userRepository.save(user);
    }

    protected Book saveTestBook(Set<User> users) {
        Book book = Book.builder()
                .title("Test Book")
                .publicationYear(Year.of(2020))
                .pageCount(100)
                .isHardcover(true)
                .users(users)
                .build();
        return bookRepository.save(book);
    }

    protected Book saveAnotherTestBook(Set<User> users) {
        Book book = Book.builder()
                .title("Another Book")
                .publicationYear(Year.of(2019))
                .pageCount(200)
                .isHardcover(false)
                .users(users)
                .deletedAt(null)
                .build();
        return bookRepository.save(book);
    }

    protected Book saveDeletedTestBook(Set<User> users) {
        Book book = Book.builder()
                .title("Deleted Book")
                .publicationYear(Year.of(2019))
                .pageCount(200)
                .isHardcover(false)
                .users(users)
                .deletedAt(Instant.now())
                .build();
        return bookRepository.save(book);
    }

    protected Book getBaseBook() {
        return Book.builder()
                .publicationYear(Year.now())
                .pageCount(34)
                .isHardcover(true)
                .users(Set.of())
                .deletedAt(null)
                .build();
    }

    protected Book saveBookWith3Authors(String bookName) {
        User activeUser = saveTestAuthor();
        User a1 = userRepository.save(activeUser.toBuilder().id(null).name("A1").build());
        User a2 = userRepository.save(activeUser.toBuilder().id(null).name("A2").build());
        User a3 = userRepository.save(activeUser.toBuilder().id(null).name("A3").build());
        Book book = getBaseBook().toBuilder()
                .title(bookName)
                .users(Set.of(a1, a2, a3))
                .build();
        return bookRepository.save(book);
    }
}
package com.example.auth.repositories;

import com.example.auth.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.id = :id AND b.deletedAt IS NULL")
    Optional<Book> findActiveById(@Param("id") Long id);

    @Query("SELECT b FROM Book b " +
            "WHERE b.id = :bookId " +
            "AND b.user.id = :userId " +
            "AND b.user.deletedAt IS NULL " +
            "AND b.active = true")
    Optional<Book> findActiveByIdForUser(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT b FROM Book b WHERE b.id IN :ids AND b.deletedAt IS NULL")
    List<Book> findActiveByIds(@Param("ids")Set<Long> ids);
}

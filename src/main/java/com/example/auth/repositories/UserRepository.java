package com.example.auth.repositories;

import com.example.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<User> findActiveById(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.id IN :ids AND u.deletedAt IS NULL")
    List<User> findActiveByIds(@Param("ids") Set<Long> ids);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deletedAT IS NULL")
    Optional<User> findActiveByUserName(@Param("username") String userName);

    boolean existsByUsernameAndDeletedAtIsNull(String username);
}

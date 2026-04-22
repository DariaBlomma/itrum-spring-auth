package com.example.auth.services;

import com.example.auth.dtos.books.BookRequest;
import com.example.auth.dtos.books.BookResponse;
import com.example.auth.entities.User;
import com.example.auth.entities.Book;
import com.example.auth.mappers.BookMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Import({BookService.class, BookMapperImpl.class})
public class BookServiceTest extends BaseServiceTest {
    @Autowired
    private BookService bookService;

    @Nested
    @DisplayName("Create tests")
    class CreateTests {
        @Test
        void shouldReturnCorrectlyMappedDTOWhenProvidedCorrectRequest() {
            User user1 = saveTestAuthor();
            User user2 = saveAnotherTestAuthor();
            Set<Long> authorIds = Set.of(user1.getId(), user2.getId());

            BookRequest request = BookRequest.builder()
                    .title("New Book")
                    .publicationYear(Year.of(2023))
                    .pageCount(150)
                    .isHardcover(false)
                    .authorIds(authorIds)
                    .build();

            BookResponse response = bookService.create(request);

            BookResponse expected = BookResponse.builder()
                    .title("New Book")
                    .publicationYear(Year.of(2023))
                    .pageCount(150)
                    .isHardcover(false)
                    .authorIds(authorIds)
                    .deletedAt(null)
                    .build();

            assertThat(response).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
        }

        @Test
        void shouldSaveAuthorsToDBWhenAuthorsNotDeleted() {
            User user1 = saveTestAuthor();
            User user2 = saveAnotherTestAuthor();
            Set<Long> authorIds = Set.of(user1.getId(), user2.getId());

            BookRequest request = BookRequest.builder()
                    .title("New Book")
                    .publicationYear(Year.of(2023))
                    .pageCount(150)
                    .isHardcover(false)
                    .authorIds(authorIds)
                    .build();

            BookResponse response = bookService.create(request);

            Book saved = bookRepository.findById(response.getId()).orElseThrow();
            Set<User> expected = Set.of(user1, user2);
            assertThat(saved.getUsers()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Get one")
    class GetOneTests {
        @Test
        void shouldReturnBookWithAuthorsWhenBookIsNotDeleted() {
            User user1 = saveTestAuthor();
            User user2 = saveAnotherTestAuthor();
            Book book = saveTestBook(Set.of(user1, user2));

            BookResponse response = bookService.getOne(book.getId());

            BookResponse expected = BookResponse.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .publicationYear(book.getPublicationYear())
                    .pageCount(book.getPageCount())
                    .isHardcover(book.getIsHardcover())
                    .authorIds(Set.of(user1.getId(), user2.getId()))
                    .deletedAt(null)
                    .build();

            assertThat(response).usingRecursiveComparison().isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Get list")
    class GetListTests {
        @Test
        void shouldReturnEmptyPageWhenNoBooksExist() {
            Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
            Page<BookResponse> result = bookService.getList(pageable);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getNumber()).isZero();
            assertThat(result.getSize()).isEqualTo(10);
        }

        @Test
        void shouldReturnCorrectTotalCountWhenMultipleAuthorsPerBook() {
            save15BooksForPaginationAndCountTest();

            long expectedTotalCount = 15;
            Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());

            Page<BookResponse> result = bookService.getList(pageable);

            assertThat(result.getTotalElements()).isEqualTo(expectedTotalCount);
            assertThat(result.getTotalPages()).isEqualTo(2); // 15 элементов / 10 = 2 страницы
        }

        @Test
        void shouldReturnDistinctBooks_WhenBookHasMultipleAuthors() {
            String bookName = "Book With 3 Authors";
            saveBookWith3Authors(bookName); // создает 3 строки в JOIN

            Page<BookResponse> result = bookService.getList(Pageable.ofSize(10));

            long count = result.getContent().stream()
                    .filter(b -> b.getTitle().equals(bookName))
                    .count();
            assertEquals(1, count, "Book should appear only once despite having 3 authors");
        }

        @Test
        void shouldExcludeDeletedAuthorsFromResponse() {
            User activeUser = saveTestAuthor();
            User deletedUser = saveDeletedTestAuthor();
            String bookName = "Book With Mixed Authors";
            Book book = getBaseBook().toBuilder()
                    .title(bookName)
                    .users(Set.of(activeUser, deletedUser))
                    .build();
            bookRepository.save(book);

            Page<BookResponse> result = bookService.getList(Pageable.ofSize(10));
            BookResponse resultBook = result.getContent().stream()
                    .filter(b -> b.getTitle().equals(bookName))
                            .findFirst()
                    .orElseThrow(() -> new AssertionError("Book not found in response"));

            assertThat(resultBook.getAuthorIds()).isEqualTo(Set.of(activeUser.getId()));
        }

        @Test
        void shouldExcludeDeletedBookFromResponse() {
            User activeUser = saveTestAuthor();
            User anotherUser = saveAnotherTestAuthor();

           saveTestBook(Set.of(activeUser));
           saveDeletedTestBook(Set.of(anotherUser, activeUser));

            Page<BookResponse> result = bookService.getList(Pageable.ofSize(10));

            boolean hasDeletedBook = result.getContent().stream()
                    .anyMatch(b -> b.getDeletedAt() != null);
            assertFalse(hasDeletedBook, "Deleted books should be excluded");
        }

        @Test
        void shouldApplyFallbackSort_WhenBookNamesAreIdentical() {
            User sharedUser = saveTestAuthor();

            Book book = Book.builder()
                    .title("First Book")
                    .publicationYear(Year.of(2024))
                    .pageCount(100)
                    .isHardcover(true)
                    .users(Set.of(sharedUser))
                    .deletedAt(null)
                    .build();
            Book book2 = book.toBuilder().build();
            Book savedBook1 = bookRepository.save(book);
            Book savedBook2 = bookRepository.save(book2);

            assertTrue(savedBook1.getId() < savedBook2.getId(),
                    "Test setup: book1 should have smaller ID than book2");

            Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
            Page<BookResponse> result = bookService.getList(pageable);

            assertEquals(2, result.getContent().size());

            // 2. Порядок по ID: book1 (меньший ID) должен идти первым
            // Это доказывает, что fallback-сортировка работает
            List<Long> actualIds = result.getContent().stream()
                    .map(BookResponse::getId)
                    .toList();

            assertEquals(List.of(savedBook1.getId(), savedBook2.getId()), actualIds,
                    "Books with same title should be sorted by ID (fallback)");
        }

        /**
         * Создает набор книг для тестирования пагинации и COUNT(DISTINCT).
         * Возвращает список созданных книг.
         * Структура:
         * - 5 книг с 1 автором
         * - 5 книг с 2 авторами (создают дубли строк при JOIN)
         * - 5 книг с 3 авторами (создают еще больше дублей)
         * Итого: 15 книг, но > 15 строк в результирующем сете JOIN.
         */
        private List<Book> save15BooksForPaginationAndCountTest() {
            List<Book> createdBooks = new ArrayList<>();

            User user1 = saveTestAuthor();
            User user2 = saveAnotherTestAuthor();
            User user3 = saveTestAuthor3();

            Set<User> singleUserSet = Set.of(user1);
            Set<User> twoAuthorsSet = Set.of(user1, user2);
            Set<User> threeAuthorsSet = Set.of(user1, user2, user3);

            // Создаем 5 книг с 1 автором
            for (int i = 0; i < 5; i++) {
                createdBooks.add(saveTestBook(singleUserSet));
            }

            // Создаем 5 книг с 2 авторами
            for (int i = 0; i < 5; i++) {
                createdBooks.add(saveTestBook(twoAuthorsSet));
            }

            // Создаем 5 книг с 3 авторами
            for (int i = 0; i < 5; i++) {
                createdBooks.add(saveTestBook(threeAuthorsSet));
            }

            return createdBooks;
        }
    }

    @Nested
    @DisplayName("Update tests")
    class UpdateTests {
        @Test
        void shouldReturnUpdatedBookWhenNotDeleted() {
            User user1 = saveTestAuthor();
            User user2 = saveAnotherTestAuthor();
            Set<User> users = Set.of(user1, user2);
            Book book = saveTestBook(users);

            Set<Long> authorIds = Set.of(user2.getId());

            BookRequest request = BookRequest.builder()
                    .title("Updated Title")
                    .publicationYear(Year.of(2024))
                    .pageCount(300)
                    .isHardcover(false)
                    .authorIds(authorIds)
                    .build();

            BookResponse response = bookService.update(book.getId(), request);

            BookResponse expected = new BookResponse(
                    book.getId(),
                    request.getTitle(),
                    request.getPublicationYear(),
                    request.getPageCount(),
                    request.getIsHardcover(),
                    request.getAuthorIds(),
                    null
            );

            assertThat(response).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        void shouldUpdateBookAuthorsWhenNewAuthorIdsValid() {
            User user1 = saveTestAuthor();
            User user2 = saveAnotherTestAuthor();
            User user3 = saveTestAuthor3();
            Set<User> initialUsers = Set.of(user1, user2);
            Book book = saveTestBook(initialUsers);

            Set<Long> newAuthorIds = Set.of(user2.getId(), user3.getId());
            Set<User> newUsers = Set.of(user2, user3);

            BookRequest request = BookRequest.builder()
                    .title(book.getTitle())
                    .publicationYear(book.getPublicationYear())
                    .pageCount(book.getPageCount())
                    .isHardcover(book.getIsHardcover())
                    .authorIds(newAuthorIds)
                    .build();

            BookResponse response = bookService.update(book.getId(), request);
            Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();

            BookResponse expected = new BookResponse(
                    book.getId(),
                    request.getTitle(),
                    request.getPublicationYear(),
                    request.getPageCount(),
                    request.getIsHardcover(),
                    newAuthorIds,
                    null
            );

            assertThat(response).usingRecursiveComparison().isEqualTo(expected);
            assertThat(updatedBook.getUsers()).isEqualTo(newUsers);
        }

        @Test
        void shouldNotUpdateBookWhenDeleted() {
            User user1 = saveTestAuthor();
            User deletedUser = saveDeletedTestAuthor();
            Book book = saveTestBook(Set.of(user1));

            Set<Long> newAuthorIds = Set.of(user1.getId(), deletedUser.getId());

            BookRequest request = BookRequest.builder()
                    .title(book.getTitle())
                    .publicationYear(book.getPublicationYear())
                    .pageCount(book.getPageCount())
                    .isHardcover(book.getIsHardcover())
                    .authorIds(newAuthorIds)
                    .build();

            try {
                bookService.update(book.getId(), request);
            } catch (RuntimeException ignored) {
            }

            Book notUpdatedBook = bookRepository.findById(book.getId()).orElseThrow();
            assertThat(notUpdatedBook).usingRecursiveComparison().isEqualTo(book);
        }
    }

    @Nested
    @DisplayName("Soft delete tests")
    class SoftDeleteTests {
        @Test
        void shouldMarkBookAsDeletedWhenExists() {
            User user = saveTestAuthor();
            Book book = saveTestBook(Set.of(user));

            bookService.deleteSoft(book.getId());

            assertThat(book.isDeleted()).isTrue();
            assertThat(book.getDeletedAt()).isNotNull();
        }

        @Test
        void shouldNotDeleteAlreadyDeletedBook() {
            User user = saveTestAuthor();
            Book deletedBook = saveDeletedTestBook(Set.of(user));
            Instant originalDeletedAt = deletedBook.getDeletedAt();

            try {
                bookService.deleteSoft(deletedBook.getId());
            } catch (RuntimeException ignored) {
            }

            assertThat(deletedBook.getDeletedAt()).isEqualTo(originalDeletedAt);
        }
    }
}

package com.cjg.book.repository;

import com.cjg.book.code.CategoryCode;
import com.cjg.book.code.ResultCode;
import com.cjg.book.code.StatusCode;
import com.cjg.book.domain.Book;
import com.cjg.book.domain.BookCategory;
import com.cjg.book.domain.Category;
import com.cjg.book.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application.properties")
public class BookCategoryRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BookCategoryRepository bookCategoryRepository;

    @Test
    @DisplayName("BookCategory 저장")
    public void save(){

        Book book = bookRepository.save(Book.builder().author("최종규").name("흑백요리사").status(StatusCode.good.name()).build());
        Category category = categoryRepository.findById(CategoryCode.cook.getCode()).orElseThrow(()->new CustomException(ResultCode.CATEGORY_SEARCH_NOT_FOUND));

        BookCategory result = bookCategoryRepository.save(BookCategory.builder().book(book).category(category).build());

        Assertions.assertThat(result.getCategory()).isEqualTo(category);
        Assertions.assertThat(result.getBook()).isEqualTo(book);
    }

    @Test
    @DisplayName("BookCategory 조회")
    public void findAllByBookBookId(){

        Book book = bookRepository.save(Book.builder().author("최종규").name("흑백요리사").status(StatusCode.good.name()).build());
        Category category1 = categoryRepository.findById(CategoryCode.cook.getCode()).orElseThrow(()->new CustomException(ResultCode.CATEGORY_SEARCH_NOT_FOUND));
        Category category2 = categoryRepository.findById(CategoryCode.cook_general.getCode()).orElseThrow(()->new CustomException(ResultCode.CATEGORY_SEARCH_NOT_FOUND));

        BookCategory result1 = bookCategoryRepository.save(BookCategory.builder().book(book).category(category1).build());
        BookCategory result2 = bookCategoryRepository.save(BookCategory.builder().book(book).category(category2).build());

        List<BookCategory> result = bookCategoryRepository.findAllByBookBookId(book.getBookId());

        Assertions.assertThat(result.get(0)).isEqualTo(result1);
        Assertions.assertThat(result.get(1)).isEqualTo(result2);
    }

    @Test
    @DisplayName("BookCategory 삭제")
    public void deleteAllByBookBookId(){

        Book book = bookRepository.save(Book.builder().author("최종규").name("흑백요리사").status(StatusCode.good.name()).build());
        Category category1 = categoryRepository.findById(CategoryCode.cook.getCode()).orElseThrow(()->new CustomException(ResultCode.CATEGORY_SEARCH_NOT_FOUND));
        Category category2 = categoryRepository.findById(CategoryCode.cook_general.getCode()).orElseThrow(()->new CustomException(ResultCode.CATEGORY_SEARCH_NOT_FOUND));

        bookCategoryRepository.save(BookCategory.builder().book(book).category(category1).build());
        bookCategoryRepository.save(BookCategory.builder().book(book).category(category2).build());

        List<BookCategory> result = bookCategoryRepository.findAllByBookBookId(book.getBookId());
        Assertions.assertThat(result.size()).isEqualTo(2);

        Long count = bookCategoryRepository.deleteAllByBookBookId(book.getBookId());
        Assertions.assertThat(count).isEqualTo(2);
    }

}

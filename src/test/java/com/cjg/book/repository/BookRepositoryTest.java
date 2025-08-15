package com.cjg.book.repository;

import com.cjg.book.code.CategoryCode;
import com.cjg.book.code.ResultCode;
import com.cjg.book.code.StatusCode;
import com.cjg.book.domain.Book;
import com.cjg.book.domain.BookCategory;
import com.cjg.book.domain.Category;
import com.cjg.book.dto.request.BookListRequestDto;
import com.cjg.book.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application.properties")
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Test
    @DisplayName("서적 저장")
    public void save(){
        Book book = Book.builder()
                .author("최종규")
                .name("흑백요리사")
                .status(StatusCode.good.name())
                .build();

        Book result = bookRepository.save(book);

        Assertions.assertThat(result.getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(result.getName()).isEqualTo(book.getName());
        Assertions.assertThat(result.getStatus()).isEqualTo(book.getStatus());
    }

    @Test
    @DisplayName("서적 조회 1건")
    public void findById(){
        Book book = Book.builder()
                .author("최종규")
                .name("흑백요리사")
                .status(StatusCode.good.name())
                .build();

        Book temp = bookRepository.save(book);
        Book result = bookRepository.findById(temp.getBookId()).orElseThrow(()->new CustomException(ResultCode.BOOK_SEARCH_NOT_FOUND));

        Assertions.assertThat(result.getBookId()).isEqualTo(temp.getBookId());
        Assertions.assertThat(result.getAuthor()).isEqualTo(temp.getAuthor());
        Assertions.assertThat(result.getName()).isEqualTo(temp.getName());
        Assertions.assertThat(result.getStatus()).isEqualTo(temp.getStatus());
    }

    @Test
    @DisplayName("서적 리스트 : 카테고리 없을 때")
    public void list_category_empty(){

        for(int i=0; i<10; i++){
            Book book = Book.builder()
                    .author("최종규")
                    .name("흑백요리사")
                    .status(StatusCode.good.name())
                    .build();

            bookRepository.save(book);

            BookCategory bookCategory1 = BookCategory.builder()
                    .book(book)
                    .category(Category.builder().categoryId(CategoryCode.cook.getCode()).name(CategoryCode.cook.name()).build()).build();

            BookCategory bookCategory2 = BookCategory.builder()
                    .book(book)
                    .category(Category.builder().categoryId(CategoryCode.cook_general.getCode()).name(CategoryCode.cook_general.name()).build()).build();

            bookCategoryRepository.save(bookCategory1);
            bookCategoryRepository.save(bookCategory2);
        }

        BookListRequestDto bookListRequestDto = BookListRequestDto.builder()
                .pageNumber(1)
                .statusCode(StatusCode.good)
                .author("최종규")
                .name("흑백요리사")
                .pageSize(10).build();

        Pageable pageable = PageRequest.of(bookListRequestDto.getPageNumber()-1, bookListRequestDto.getPageSize(), Sort.Direction.DESC, "regDate");

        Page<Book> result = bookRepository.list(pageable, bookListRequestDto);

        Assertions.assertThat(result.getContent().size()).isEqualTo(10);
    }


    @Test
    @DisplayName("서적 리스트 : 카테고리 있을 때")
    public void list_category_add(){

        for(int i=0; i<10; i++){
            Book book = Book.builder()
                    .author("최종규")
                    .name("흑백요리사")
                    .status(StatusCode.good.name())
                    .build();

            bookRepository.save(book);

            BookCategory bookCategory1 = BookCategory.builder()
                    .book(book)
                    .category(Category.builder().categoryId(CategoryCode.cook.getCode()).name(CategoryCode.cook.name()).build()).build();

            BookCategory bookCategory2 = BookCategory.builder()
                    .book(book)
                    .category(Category.builder().categoryId(CategoryCode.cook_general.getCode()).name(CategoryCode.cook_general.name()).build()).build();

            bookCategoryRepository.save(bookCategory1);
            bookCategoryRepository.save(bookCategory2);
        }

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook_general);

        BookListRequestDto bookListRequestDto = BookListRequestDto.builder()
                .pageNumber(1)
                .statusCode(StatusCode.good)
                .author("최종규")
                .name("흑백요리사")
                .categoryCodeList(categoryCodeList)
                .pageSize(10).build();

        Pageable pageable = PageRequest.of(bookListRequestDto.getPageNumber()-1, bookListRequestDto.getPageSize(), Sort.Direction.DESC, "regDate");

        Page<Book> result = bookRepository.list(pageable, bookListRequestDto);

        Assertions.assertThat(result.getContent().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("서적 삭제")
    public void delete(){

        Book book = Book.builder()
                .author("최종규")
                .name("흑백요리사")
                .status(StatusCode.good.name())
                .build();

        Book saveResult = bookRepository.save(book);

        bookRepository.delete(saveResult);
        Optional<Book> optional =  bookRepository.findById(book.getBookId());
        Assertions.assertThatThrownBy(optional::get).isInstanceOf(NoSuchElementException.class);
    }

}

package com.cjg.book.service;

import com.cjg.book.code.CategoryCode;
import com.cjg.book.domain.Book;
import com.cjg.book.domain.BookCategory;
import com.cjg.book.domain.Category;
import com.cjg.book.repository.BookCategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BookCategoryServiceTest {

    @InjectMocks
    private BookCategoryService bookCategoryService;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @Test
    @DisplayName("서적_카테고리 저장 : 성공")
    public void save_ok(){

        Book book = Book.builder()
                .bookId(1L)
                .author("최종규")
                .name("흑백요리사")
                .regDate(LocalDateTime.now())
                .build();

        Category category= Category.builder().categoryId(CategoryCode.cook.getCode()).name(CategoryCode.cook.name()).build();

        BookCategory bookCategory = BookCategory.builder().bookCategoryId(1L)
                .book(book)
                .category(category)
                .build();

        given(bookCategoryRepository.save(bookCategory)).willReturn(bookCategory);
        BookCategory result = bookCategoryService.save(bookCategory);

        Assertions.assertThat(result).isEqualTo(bookCategory);
    }

    @Test
    @DisplayName("서적_카테고리 리스트 조회 : 성공")
    public void findAllByBookBookId_ok(){

        Book book = Book.builder()
                .bookId(1L)
                .author("최종규")
                .name("흑백요리사")
                .regDate(LocalDateTime.now())
                .build();

        Category category1= Category.builder().categoryId(CategoryCode.cook.getCode()).name(CategoryCode.cook.name()).build();
        Category category2= Category.builder().categoryId(CategoryCode.cook_general.getCode()).name(CategoryCode.cook_general.name()).build();

        BookCategory bookCategory1 = BookCategory.builder().bookCategoryId(1L)
                .book(book)
                .category(category1)
                .build();

        BookCategory bookCategory2 = BookCategory.builder().bookCategoryId(2L)
                .book(book)
                .category(category2)
                .build();

        List<BookCategory> list = new ArrayList<>();
        list.add(bookCategory1);
        list.add(bookCategory2);

        given(bookCategoryRepository.findAllByBookBookId(book.getBookId())).willReturn(list);
        List<BookCategory> result = bookCategoryService.findAllByBookBookId(book.getBookId());

        Assertions.assertThat(result.getFirst()).isEqualTo(list.getFirst());
        Assertions.assertThat(result.getLast()).isEqualTo(list.getLast());
    }

    @Test
    @DisplayName("서적_카테고리 삭제 : 성공")
    public void deleteAllByBookBookId_ok(){
        long bookId = 2L;
        given(bookCategoryRepository.deleteAllByBookBookId(bookId)).willReturn(1L);
        Long result = bookCategoryService.deleteAllByBookBookId(bookId);
        Assertions.assertThat(result).isEqualTo(1L);
    }

}

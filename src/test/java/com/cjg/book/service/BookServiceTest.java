package com.cjg.book.service;


import com.cjg.book.code.CategoryCode;
import com.cjg.book.code.StatusCode;
import com.cjg.book.domain.Book;
import com.cjg.book.domain.BookCategory;
import com.cjg.book.domain.Category;
import com.cjg.book.dto.request.BookDeleteRequestDto;
import com.cjg.book.dto.request.BookListRequestDto;
import com.cjg.book.dto.request.BookModifyRequestDto;
import com.cjg.book.dto.request.BookSaveRequestDto;
import com.cjg.book.dto.response.BookListResponseDto;
import com.cjg.book.dto.response.BookResponseDto;
import com.cjg.book.exception.CustomException;
import com.cjg.book.repository.BookRepository;
import com.cjg.book.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookCategoryService bookCategoryService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Test
    @DisplayName("서적 저장 : 성공")
    public void save_ok(){

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook_general);

        BookSaveRequestDto bookSaveRequestDto = BookSaveRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        Book book = Book.builder()
                .bookId(1L)
                .author(bookSaveRequestDto.getAuthor())
                .name(bookSaveRequestDto.getName())
                .status(bookSaveRequestDto.getStatusCode().name())
                .regDate(LocalDateTime.now())
                .build();

        given(bookRepository.save(any(Book.class))).willReturn(book);

        BookResponseDto result = bookService.save(bookSaveRequestDto);

        Assertions.assertThat(result.getBookId()).isEqualTo(book.getBookId());
        Assertions.assertThat(result.getCategoryCodeList()).isEqualTo(categoryCodeList);
        Assertions.assertThat(result.getAuthor()).isEqualTo(bookSaveRequestDto.getAuthor());
        Assertions.assertThat(result.getName()).isEqualTo(bookSaveRequestDto.getName());
        Assertions.assertThat(result.getStatusCode()).isEqualTo(bookSaveRequestDto.getStatusCode());
    }

    @Test
    @DisplayName("서적 1개 검색 : 성공")
    public void findById_ok(){

        Book book = Book.builder()
                .bookId(1L)
                .author("최종규")
                .name("흑백요리사")
                .status(StatusCode.good.name())
                .regDate(LocalDateTime.now())
                .build();

        List<BookCategory> bookCategoryList = new ArrayList<>();

        BookCategory bookCategory1 = BookCategory.builder()
                .bookCategoryId(1L)
                .book(book)
                .category(Category.builder().categoryId(CategoryCode.cook.getCode()).name(CategoryCode.cook.name()).build()).build();

        BookCategory bookCategory2 = BookCategory.builder()
                .bookCategoryId(1L)
                .book(book)
                .category(Category.builder().categoryId(CategoryCode.cook_general.getCode()).name(CategoryCode.cook_general.name()).build()).build();

        bookCategoryList.add(bookCategory1);
        bookCategoryList.add(bookCategory2);

        given(bookRepository.findById(book.getBookId())).willReturn(Optional.of(book));
        given(bookCategoryService.findAllByBookBookId(book.getBookId())).willReturn(bookCategoryList);

        BookResponseDto result = bookService.findById(1L);

        Assertions.assertThat(result.getBookId()).isEqualTo(book.getBookId());

    }

    @Test
    @DisplayName("서적 1개 검색 : 실패 : 서적 없을 때")
    public void findById_fail_null(){
        given(bookRepository.findById(1L)).willReturn(Optional.empty());
        Assertions.assertThatThrownBy(()-> bookService.findById(1L)).isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("서적 리스트 검색 : 성공")
    public void list_ok(){

        final long totalCount = 30;
        final int pageNumber = 2;
        final int pageSize = 10;

        BookListRequestDto bookListRequestDto = BookListRequestDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize).build();

        List<Book> bookList = new ArrayList<>();
        Map<Long, List<BookCategory>> bookCategoryMap = new HashMap<>();

        for(int i=0; i<bookListRequestDto.getPageSize(); i++) {
            Book book = Book.builder()
                    .bookId((long)i)
                    .author("최종규" + i)
                    .name("흑백요리사" + i)
                    .status(StatusCode.good.name())
                    .regDate(LocalDateTime.now())
                    .build();

            bookList.add(0, book);

            List<BookCategory> bookCategoryList = new ArrayList<>();

            BookCategory bookCategory1 = BookCategory.builder()
                    .bookCategoryId((long)i)
                    .book(book)
                    .category(Category.builder().categoryId(CategoryCode.cook.getCode()).name(CategoryCode.cook.name()).build()).build();

            BookCategory bookCategory2 = BookCategory.builder()
                    .bookCategoryId((long)i+1)
                    .book(book)
                    .category(Category.builder().categoryId(CategoryCode.cook_general.getCode()).name(CategoryCode.cook_general.name()).build()).build();

            bookCategoryList.add(bookCategory1);
            bookCategoryList.add(bookCategory2);

            bookCategoryMap.put((long)i, bookCategoryList);
        }

        Pageable pageable = PageRequest.of(bookListRequestDto.getPageNumber()-1, bookListRequestDto.getPageSize(), Sort.Direction.DESC, "regDate");
        Page<Book> page = new PageImpl<>(bookList, pageable, totalCount);

        given(bookRepository.list(pageable, bookListRequestDto)).willReturn(page);

        for(int i=0; i<bookListRequestDto.getPageSize(); i++){
            given(bookCategoryService.findAllByBookBookId((long)i)).willReturn(bookCategoryMap.get((long)i));
        }

        BookListResponseDto result = bookService.list(bookListRequestDto);

        Assertions.assertThat(result.getBookList().size()).isEqualTo(bookListRequestDto.getPageSize());
        Assertions.assertThat(result.getPageNumber()).isEqualTo(bookListRequestDto.getPageNumber());
        Assertions.assertThat(result.getTotalPage()).isEqualTo(totalCount/bookListRequestDto.getPageSize());
        Assertions.assertThat(result.getTotalCount()).isEqualTo(totalCount);
        Assertions.assertThat(result.getNextPage()).isEqualTo("/v1/book/list?pageNumber=" + (pageNumber+1) + "&pageSize=" + pageSize);
        Assertions.assertThat(result.getPrevPage()).isEqualTo("/v1/book/list?pageNumber=" + (pageNumber-1) + "&pageSize=" + pageSize);
    }


    @Test
    @DisplayName("이전페이지 가져오기 : 성공")
    public void getPageUrl_prev_ok() {

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook_general);

        BookListRequestDto bookListRequestDto = BookListRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .statusCode(StatusCode.good)
                .author("최종규")
                .name("흑백요리사")
                .pageNumber(2)
                .pageSize(10).build();

        String result = bookService.getPageUrl(bookListRequestDto, "prev", 10);
        Assertions.assertThat(result).isEqualTo("/v1/book/list?categoryCodeList=cook,cook_general&statusCode=good&author=최종규&name=흑백요리사&pageNumber=1&pageSize=10");
    }

    @Test
    @DisplayName("이전페이지 가져오기 : 성공 : 이전페이지 없을 때")
    public void getPageUrl_prev_ok_empty() {

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook_general);

        BookListRequestDto bookListRequestDto = BookListRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .statusCode(StatusCode.good)
                .author("최종규")
                .name("흑백요리사")
                .pageNumber(1)
                .pageSize(10).build();

        String result = bookService.getPageUrl(bookListRequestDto, "prev", 10);
        Assertions.assertThat(result).isEqualTo("");
    }

    @Test
    @DisplayName("다음페이지 가져오기 : 성공")
    public void getPageUrl_next_ok() {

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook_general);

        BookListRequestDto bookListRequestDto = BookListRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .statusCode(StatusCode.good)
                .author("최종규")
                .name("흑백요리사")
                .pageNumber(1)
                .pageSize(10).build();

        String result = bookService.getPageUrl(bookListRequestDto, "next", 10);
        Assertions.assertThat(result).isEqualTo("/v1/book/list?categoryCodeList=cook,cook_general&statusCode=good&author=최종규&name=흑백요리사&pageNumber=2&pageSize=10");
    }

    @Test
    @DisplayName("다음페이지 가져오기 : 성공 : 다음페이지 없을 때")
    public void getPageUrl_next_ok_empty() {

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook);

        BookListRequestDto bookListRequestDto = BookListRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .statusCode(StatusCode.good)
                .author("최종규")
                .name("흑백요리사")
                .pageNumber(10)
                .pageSize(10).build();

        String result = bookService.getPageUrl(bookListRequestDto, "next", 10);
        Assertions.assertThat(result).isEqualTo("");
    }

    @Test
    @DisplayName("서적 수정 : 성공")
    public void modify_ok() {

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook_general);

        BookModifyRequestDto bookModifyRequestDto = BookModifyRequestDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        Book book = Book.builder()
                .bookId(bookModifyRequestDto.getBookId())
                .author(bookModifyRequestDto.getAuthor())
                .name(bookModifyRequestDto.getName())
                .status(bookModifyRequestDto.getStatusCode().name())
                .regDate(LocalDateTime.now())
                .build();

        given(bookRepository.findById(bookModifyRequestDto.getBookId())).willReturn(Optional.of(book));

        BookResponseDto bookResponseDto = bookService.modify(bookModifyRequestDto);

        Assertions.assertThat(bookResponseDto.getBookId()).isEqualTo(bookModifyRequestDto.getBookId());
        Assertions.assertThat(bookResponseDto.getCategoryCodeList()).isEqualTo(categoryCodeList);
        Assertions.assertThat(bookResponseDto.getAuthor()).isEqualTo(bookModifyRequestDto.getAuthor());
        Assertions.assertThat(bookResponseDto.getName()).isEqualTo(bookModifyRequestDto.getName());
        Assertions.assertThat(bookResponseDto.getStatusCode()).isEqualTo(bookModifyRequestDto.getStatusCode());

    }

    @Test
    @DisplayName("서적 수정 : 실패 : 해당 bookId가 없을 때")
    public void modify_fail_null() {
        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook_general);

        BookModifyRequestDto bookModifyRequestDto = BookModifyRequestDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        given(bookRepository.findById(1L)).willReturn(Optional.empty());

        Assertions.assertThatThrownBy(()-> bookService.modify(bookModifyRequestDto)).isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("서적 삭제 : 성공")
    public void delete_ok(){
        BookDeleteRequestDto bookDeleteRequestDto = BookDeleteRequestDto.builder().bookId(1L).build();

        Book book = Book.builder()
                        .bookId(bookDeleteRequestDto.getBookId())
                        .author("최종규")
                        .name("흑백요리사")
                        .regDate(LocalDateTime.now())
                        .build();

        given(bookRepository.findById(bookDeleteRequestDto.getBookId())).willReturn(Optional.ofNullable(book));
        bookService.delete(bookDeleteRequestDto);
    }

    @Test
    @DisplayName("서적 삭제 : 실패 : 서적 없을때")
    public void delete_fail_null(){
        BookDeleteRequestDto bookDeleteRequestDto = BookDeleteRequestDto.builder().bookId(1L).build();
        given(bookRepository.findById(bookDeleteRequestDto.getBookId())).willReturn(Optional.empty());
        Assertions.assertThatThrownBy(()-> bookService.delete(bookDeleteRequestDto)).isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("BookSaveRequestDto > Book 변환")
    public void toBook() {
        BookSaveRequestDto bookDto = BookSaveRequestDto.builder()
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        Book result = bookService.toBook(bookDto);

        Assertions.assertThat(result.getAuthor()).isEqualTo(bookDto.getAuthor());
        Assertions.assertThat(result.getName()).isEqualTo(bookDto.getName());
        Assertions.assertThat(result.getStatus()).isEqualTo(bookDto.getStatusCode().name());
    }

    @Test
    @DisplayName("Book > BookSaveRequestDto 변환")
    public void toBookResponseDto() {
        Book book = Book.builder()
                .bookId(1L)
                .author("최종규")
                .name("흑백요리사")
                .status(StatusCode.good.name())
                .regDate(LocalDateTime.now())
                .build();

        BookResponseDto result = bookService.toBookResponseDto(book);

        Assertions.assertThat(result.getBookId()).isEqualTo(book.getBookId());
        Assertions.assertThat(result.getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(result.getName()).isEqualTo(book.getName());
        Assertions.assertThat(result.getStatusCode().name()).isEqualTo(book.getStatus());
    }
}

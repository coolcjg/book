package com.cjg.book.controller;

import com.cjg.book.code.CategoryCode;
import com.cjg.book.code.StatusCode;
import com.cjg.book.config.security.SecurityConfig;
import com.cjg.book.dto.request.BookDeleteRequestDto;
import com.cjg.book.dto.request.BookListRequestDto;
import com.cjg.book.dto.request.BookModifyRequestDto;
import com.cjg.book.dto.request.BookSaveRequestDto;
import com.cjg.book.dto.response.BookListResponseDto;
import com.cjg.book.dto.response.BookResponseDto;
import com.cjg.book.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
단위 테스트 진행
includeFilters를 사용하여 현재 사용중인 security필터를 등록하여 401, 403에러를 제외한다.
*/
@WebMvcTest(
    controllers = BookController.class
    , includeFilters={
        @ComponentScan.Filter(
                type= FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
    }
)
public class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    BookService bookService;

    @Test
    @DisplayName("서적 저장 : 성공")
    void save_ok() throws Exception {

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook_general);

        BookSaveRequestDto bookSaveRequestDto = BookSaveRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        BookResponseDto bookResponseDto = BookResponseDto.builder()
                .bookId(1L)
                .categoryCodeList(bookSaveRequestDto.getCategoryCodeList())
                .author(bookSaveRequestDto.getAuthor())
                .name(bookSaveRequestDto.getName())
                .statusCode(bookSaveRequestDto.getStatusCode())
                .build();

        given(bookService.save(any(BookSaveRequestDto.class))).willReturn(bookResponseDto);

        String json = objectMapper.writeValueAsString(bookSaveRequestDto);

        mvc.perform(
                post("/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("201"))
            .andExpect(jsonPath("$.data.bookId").value(bookResponseDto.getBookId()))
            .andExpect(jsonPath("$.data.categoryCodeList").value(Matchers.contains(
                    categoryCodeList.stream().map(CategoryCode::toString).toArray(String[]::new)
            )))
            .andExpect(jsonPath("$.data.author").value(bookResponseDto.getAuthor()))
            .andExpect(jsonPath("$.data.name").value(bookResponseDto.getName()))
            .andExpect(jsonPath("$.data.statusCode").value(bookResponseDto.getStatusCode().name()))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 저장 실패 : 카테고리 null")
    void save_fail_bookCategory_null() throws Exception{

        BookSaveRequestDto bookSaveRequestDto = BookSaveRequestDto.builder()
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(bookSaveRequestDto);

        mvc.perform(
                post("/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 저장 : 실패 : 카테고리 빈값")
    void save_fail_bookCategory_empty() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();

        BookSaveRequestDto bookSaveRequestDto = BookSaveRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(bookSaveRequestDto);

        mvc.perform(
                post("/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 저장 : 실패 : 중복된 카테고리")
    void save_fail_bookCategory_duplicated() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook);

        BookSaveRequestDto bookSaveRequestDto = BookSaveRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(bookSaveRequestDto);

        mvc.perform(
                post("/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 저장 : 실패 : 지은이 null")
    void save_fail_author_null() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookSaveRequestDto bookSaveRequestDto = BookSaveRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(bookSaveRequestDto);

        mvc.perform(
                    post("/v1/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 저장 : 실패 : 지은이 공백")
    void save_fail_author_blank() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookSaveRequestDto bookSaveRequestDto = BookSaveRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .author("")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(bookSaveRequestDto);

        mvc.perform(
                    post("/v1/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 저장 : 실패 : 제목 null")
    void save_fail_name_null() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookSaveRequestDto bookSaveRequestDto = BookSaveRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(bookSaveRequestDto);

        mvc.perform(
                post("/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 저장 : 실패 : 제목 공백")
    void save_fail_name_blank() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookSaveRequestDto bookSaveRequestDto = BookSaveRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name(" ")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(bookSaveRequestDto);

        mvc.perform(
                post("/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 저장 : 실패 : 상태 null")
    void save_fail_status_null() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookSaveRequestDto bookSaveRequestDto = BookSaveRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .build();

        String json = objectMapper.writeValueAsString(bookSaveRequestDto);

        mvc.perform(post("/v1/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 수정 : 성공")
    void modify_ok() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookModifyRequestDto dto = BookModifyRequestDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        BookResponseDto bookResponseDto = BookResponseDto.builder()
                .bookId(dto.getBookId())
                .categoryCodeList(dto.getCategoryCodeList())
                .author(dto.getAuthor())
                .name(dto.getName())
                .statusCode(dto.getStatusCode())
                .build();

        String json = objectMapper.writeValueAsString(dto);

        given(bookService.modify(any(BookModifyRequestDto.class))).willReturn(bookResponseDto);

        mvc.perform(put("/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.bookId").value(bookResponseDto.getBookId()))
                .andExpect(jsonPath("$.data.categoryCodeList").value(
                        categoryCodeList.stream().map(CategoryCode::toString).toArray(String[]::new)[0]
                ))
                .andExpect(jsonPath("$.data.author").value(bookResponseDto.getAuthor()))
                .andExpect(jsonPath("$.data.name").value(bookResponseDto.getName()))
                .andExpect(jsonPath("$.data.statusCode").value(bookResponseDto.getStatusCode().name()))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 수정 : 실패 : 카테고리 null")
    void modify_fail_bookCategory_null() throws Exception{

        BookModifyRequestDto dto = BookModifyRequestDto.builder()
                .bookId(1L)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mvc.perform(put("/v1/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 수정 : 실패 : 카테고리 빈값")
    void modify_fail_bookCategory_empty() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();

        BookModifyRequestDto dto = BookModifyRequestDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mvc.perform(put("/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 수정 : 실패 : 중복된 카테고리")
    void modify_fail_bookCategory_duplicated() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook);

        BookModifyRequestDto dto = BookModifyRequestDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mvc.perform(put("/v1/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 수정 : 실패 : 지은이 null")
    void modify_fail_author_null() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookModifyRequestDto dto = BookModifyRequestDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mvc.perform(put("/v1/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 수정 : 실패 : 지은이 공백")
    void modify_fail_author_blank() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookModifyRequestDto bookSaveRequestDto = BookModifyRequestDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .author("")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(bookSaveRequestDto);

        mvc.perform(put("/v1/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 수정 : 실패 : 제목 null")
    void modify_fail_name_null() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookModifyRequestDto dto = BookModifyRequestDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mvc.perform(put("/v1/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 수정 : 실패 : 제목 공백")
    void modify_fail_name_blank() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookModifyRequestDto dto = BookModifyRequestDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("")
                .statusCode(StatusCode.good)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mvc.perform(put("/v1/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andDo(print());
    }

    @Test
    @DisplayName("서적 수정 : 실패 : 상태 null")
    void modify_fail_status_null() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookModifyRequestDto dto = BookModifyRequestDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mvc.perform(put("/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 개별 검색 : 성공")
    public void findById_ok() throws Exception{

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);

        BookResponseDto bookResponseDto = BookResponseDto.builder()
                .bookId(1L)
                .categoryCodeList(categoryCodeList)
                .author("최종규")
                .name("흑백요리사")
                .statusCode(StatusCode.good)
                .regDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        given(bookService.findById(1L)).willReturn(bookResponseDto);

        mvc.perform(get("/v1/book/" + bookResponseDto.getBookId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.bookId").value(bookResponseDto.getBookId()))
                .andExpect(jsonPath("$.data.categoryCodeList").value(Matchers.contains(
                        categoryCodeList.stream().map(CategoryCode::toString).toArray(String[]::new)
                )))
                .andExpect(jsonPath("$.data.author").value(bookResponseDto.getAuthor()))
                .andExpect(jsonPath("$.data.name").value(bookResponseDto.getName()))
                .andExpect(jsonPath("$.data.statusCode").value(bookResponseDto.getStatusCode().name()))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 개별 검색 : 실패 : 숫자가 아닌값")
    public void findById_fail_invalidBookId() throws Exception{
        mvc.perform(get("/v1/book/--"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 리스트 검색 : 성공 : 검색조건 없을 때")
    public void list_ok_emptyParam() throws Exception{

        List<BookResponseDto> booklist = new ArrayList<>();

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook_general);

        for(int i=0; i<10; i++){
            BookResponseDto bookResponseDto = BookResponseDto.builder()
                    .categoryCodeList(categoryCodeList)
                    .bookId((long)i)
                    .author("최종규"+i)
                    .name("흑백요리사"+i)
                    .regDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build();
            booklist.add(bookResponseDto);
        }

        BookListResponseDto bookListResponseDto = BookListResponseDto.builder()
                .bookList(booklist).build();

        given(bookService.list(any(BookListRequestDto.class))).willReturn(bookListResponseDto);

        mvc.perform(get("/v1/book/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.bookList.length()").value(booklist.size()))
                .andDo(print());
    }


    @Test
    @DisplayName("서적 리스트 검색 : 성공 : 검색조건 모두 있을 때")
    public void list_ok_fullParam() throws Exception{

        String sb = "categoryCodeList=" + CategoryCode.cook.name() + "," + CategoryCode.cook_general.name() +
                "&statusCode=" + StatusCode.good.name() +
                "&author=최종규" +
                "&name=흑백요리사" +
                "&pageNumber=1" +
                "&pageSize=10";

        List<BookResponseDto> booklist = new ArrayList<>();

        List<CategoryCode> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(CategoryCode.cook);
        categoryCodeList.add(CategoryCode.cook_general);

        for(int i=0; i<10; i++){
            BookResponseDto bookResponseDto = BookResponseDto.builder()
                    .categoryCodeList(categoryCodeList)
                    .bookId((long)i)
                    .author("최종규"+i)
                    .name("흑백요리사"+i)
                    .regDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build();
            booklist.add(bookResponseDto);
        }

        BookListResponseDto bookListResponseDto = BookListResponseDto.builder()
                .bookList(booklist).build();

        given(bookService.list(any(BookListRequestDto.class))).willReturn(bookListResponseDto);

        mvc.perform(get("/v1/book/list?"+ sb))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.bookList.length()").value(booklist.size()))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 리스트 검색 : 실패 : 카테고리 없는 값")
    public void list_fail_categoryCodeList_invalid() throws Exception{
        mvc.perform(get("/v1/book/list?categoryCodeList=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 리스트 검색 : 실패 : 상태 없는 값")
    public void list_fail_statusCode_invalid() throws Exception{
        mvc.perform(get("/v1/book/list?statusCode=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }


    @Test
    @DisplayName("서적 리스트 검색 : 실패 : 지은이 빈 값")
    public void list_fail_author_blank() throws Exception{
        mvc.perform(get("/v1/book/list?author= "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 리스트 검색 : 실패 : 제목 빈 값")
    public void list_fail_name_blank() throws Exception{
        mvc.perform(get("/v1/book/list?name= "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 리스트 검색 : 실패 : pageNumber 문자로 들어올때")
    public void list_fail_pageNumber_invalid() throws Exception{
        mvc.perform(get("/v1/book/list?pageNumber=--"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 리스트 검색 : 실패 : pageNumber 양수 아닐때")
    public void list_fail_pageNumber_notPositive() throws Exception{
        mvc.perform(get("/v1/book/list?pageNumber=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 리스트 검색 : 실패 : pageSize 문자로 들어올때")
    public void list_fail_pageSize_invalid() throws Exception{
        mvc.perform(get("/v1/book/list?pageSize=--"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 리스트 검색 : 실패 : pageSize 양수 아닐때")
    public void list_fail_pageSize_notPositive() throws Exception{
        mvc.perform(get("/v1/book/list?pageSize=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());
    }

    @Test
    @DisplayName("서적 삭제 : 성공")
    public void delete_ok() throws Exception{

        BookDeleteRequestDto dto = BookDeleteRequestDto.builder()
                .bookId(1L)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mvc.perform(delete("/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

}

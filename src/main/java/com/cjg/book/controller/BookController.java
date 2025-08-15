package com.cjg.book.controller;



import com.cjg.book.code.CategoryCode;
import com.cjg.book.code.ResultCode;
import com.cjg.book.code.StatusCode;
import com.cjg.book.dto.request.BookDeleteRequestDto;
import com.cjg.book.dto.request.BookListRequestDto;
import com.cjg.book.dto.request.BookModifyRequestDto;
import com.cjg.book.dto.request.BookSaveRequestDto;
import com.cjg.book.dto.response.BookListResponseDto;
import com.cjg.book.dto.response.BookResponseDto;
import com.cjg.book.exception.CustomException;
import com.cjg.book.response.Response;
import com.cjg.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping(value = "/v1/book")
    @Operation(summary = "서적 저장")
    public ResponseEntity<Response<BookResponseDto>> save(@RequestBody @Valid BookSaveRequestDto bookDto){
        checkDuplicatedCode(bookDto.getCategoryCodeList());
        return ResponseEntity.ok(Response.success(ResultCode.BOOK_SAVE_SUCCESS, bookService.save(bookDto)));
    }

    @GetMapping(value = "/v1/book/{bookId}")
    @Operation(summary = "서적 개별 조회")
    public ResponseEntity<Response<BookResponseDto>> findById(@PathVariable("bookId") long bookId){

        System.out.println("11");
        System.out.println("22");
        System.out.println("33");
        return ResponseEntity.ok(Response.success(ResultCode.BOOK_SEARCH_ONE_OK, bookService.findById(bookId)));
    }

    @GetMapping(value = "/v1/book/list")
    @Operation(summary = "서적 리스트 조회")
    public ResponseEntity<Response<BookListResponseDto>> list(
            @RequestParam(required = false) List<CategoryCode> categoryCodeList
            ,@RequestParam(required = false) StatusCode statusCode
            ,@RequestParam(required = false) String author
            ,@RequestParam(required = false) String name
            ,@RequestParam(required = false, defaultValue = "1") Integer pageNumber
            ,@RequestParam(required = false, defaultValue = "10") Integer pageSize
    ){

        BookListRequestDto bookListRequestDto = BookListRequestDto.builder()
                .categoryCodeList(categoryCodeList)
                .statusCode(statusCode)
                .author(author)
                .name(name)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        bookListRequestDto.checkParam();

        return ResponseEntity.ok(Response.success(ResultCode.BOOK_SEARCH_LIST_OK, bookService.list(bookListRequestDto)));
    }

    @PutMapping(value = "/v1/book")
    @Operation(summary = "서적 수정")
    public ResponseEntity<Response<BookResponseDto>> modify(@RequestBody @Valid BookModifyRequestDto bookModifyRequestDto){
        checkDuplicatedCode(bookModifyRequestDto.getCategoryCodeList());
        return ResponseEntity.ok(Response.success(ResultCode.BOOK_MODIFY_SUCCESS, bookService.modify(bookModifyRequestDto)));
    }

    @DeleteMapping(value = "/v1/book")
    @Operation(summary = "서적 삭제")
    public ResponseEntity<Response<Void>> delete(@RequestBody @Valid BookDeleteRequestDto bookDeleteRequestDto){
        bookService.delete(bookDeleteRequestDto);
        return ResponseEntity.ok(Response.success(ResultCode.BOOK_DELETE_SUCCESS));
    }

    //카테고리 코드가 중복된 값이 있는지 체크
    public void checkDuplicatedCode(List<?> list){
        Set<?> set = new HashSet<>(list);
        if(list.size() != set.size()) throw new CustomException(ResultCode.BOOK_INVALID_CATEGORY);
    }

}

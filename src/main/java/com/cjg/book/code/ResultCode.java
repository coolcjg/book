package com.cjg.book.code;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Getter
public enum ResultCode {

    BOOK_SAVE_SUCCESS(HttpStatus.CREATED, "도서 저장 성공"),
    BOOK_SEARCH_ONE_OK(HttpStatus.OK, "도서 개별 조회 성공"),
    BOOK_SEARCH_LIST_OK(HttpStatus.OK, "도서 리스트 조회 성공"),
    BOOK_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "도서가 없습니다"),
    BOOK_MODIFY_SUCCESS(HttpStatus.OK, "도서 수정 성공"),
    BOOK_DELETE_SUCCESS(HttpStatus.OK, "도서 삭제 성공"),

    BOOK_INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "중복된 카테고리값입니다."),
    BOOK_INVALID_AUTHOR(HttpStatus.BAD_REQUEST, "지은이가 적합하지 않습니다."),
    BOOK_INVALID_NAME(HttpStatus.BAD_REQUEST, "제목이 적합하지 않습니다."),

    BOOK_INVALID_PARAM(HttpStatus.BAD_REQUEST, "적합하지 않은 파라미터입니다."),

    CATEGORY_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리가 없습니다"),

    PAGE_INVALID_SIZE(HttpStatus.BAD_REQUEST, "페이지 사이즈가 적합하지 않습니다"),
    PAGE_INVALID_NUMBER(HttpStatus.BAD_REQUEST, "페이지 번호가 적합하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;

    public String getCode() {
        return String.valueOf(httpStatus.value());
    }
}

package com.cjg.book.dto.response;

import com.cjg.book.code.CategoryCode;
import com.cjg.book.code.StatusCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@Setter
@Getter
@ToString
public class BookResponseDto {

    private Long bookId;
    private List<CategoryCode> categoryCodeList;
    private String author;
    private String name;
    private StatusCode statusCode;
    private String regDate;
    private String modDate;
}

package com.cjg.book.dto.request;

import com.cjg.book.code.CategoryCode;
import com.cjg.book.code.StatusCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BookModifyRequestDto {

    @NotNull(message = "서적ID를 입력하세요")
    private Long bookId;

    @NotNull(message = "카테고리 코드를 입력하세요")
    @NotEmpty(message = "카테고리 코드를 입력하세요")
    private List<CategoryCode> categoryCodeList;

    @NotBlank(message = "지은이를 입력하세요")
    private String author;

    @NotBlank(message = "제목을 입력하세요")
    private String name;

    @NotNull(message = "상태코드를 입력하세요")
    private StatusCode statusCode;

}

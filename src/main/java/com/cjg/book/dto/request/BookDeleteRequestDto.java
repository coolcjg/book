package com.cjg.book.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookDeleteRequestDto {

    @NotNull(message = "서적ID를 입력하세요")
    private Long bookId;

}

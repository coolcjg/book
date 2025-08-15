package com.cjg.book.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
public class BookListResponseDto {
    List<BookResponseDto> bookList;
    int pageNumber;
    int totalPage;
    Long totalCount;
    String nextPage;
    String prevPage;
}

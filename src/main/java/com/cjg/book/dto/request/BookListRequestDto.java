package com.cjg.book.dto.request;


import com.cjg.book.code.CategoryCode;
import com.cjg.book.code.ResultCode;
import com.cjg.book.code.StatusCode;
import com.cjg.book.exception.CustomException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class BookListRequestDto {

    private List<CategoryCode> categoryCodeList;
    private String author;
    private String name;
    private StatusCode statusCode;

    private Integer pageNumber;
    private Integer pageSize;

    public void checkParam(){
        if(author != null && author.isBlank()){
            throw new CustomException(ResultCode.BOOK_INVALID_AUTHOR);
        }

        if(name != null && name.isBlank()){
            throw new CustomException(ResultCode.BOOK_INVALID_NAME);
        }

        if(pageNumber <= 0 ){
            throw new CustomException(ResultCode.PAGE_INVALID_NUMBER);
        }

        if(pageSize <= 0 ){
            throw new CustomException(ResultCode.PAGE_INVALID_SIZE);
        }
    }
}

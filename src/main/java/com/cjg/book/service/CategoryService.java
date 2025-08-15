package com.cjg.book.service;



import com.cjg.book.code.ResultCode;
import com.cjg.book.domain.Category;
import com.cjg.book.exception.CustomException;
import com.cjg.book.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findById(Long id){
        return categoryRepository.findById(id).orElseThrow(()-> new CustomException(ResultCode.CATEGORY_SEARCH_NOT_FOUND));
    }
}

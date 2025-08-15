package com.cjg.book.service;

import com.cjg.book.domain.BookCategory;
import com.cjg.book.repository.BookCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCategoryService {

    private final BookCategoryRepository bookCategoryRepository;

    public BookCategory save(BookCategory bookCategory){
        return bookCategoryRepository.save(bookCategory);
    }

    public List<BookCategory> findAllByBookBookId(Long bookId){ return bookCategoryRepository.findAllByBookBookId(bookId); }

    public Long deleteAllByBookBookId(Long bookId){
        return bookCategoryRepository.deleteAllByBookBookId(bookId);
    }

    public void flush(){
        bookCategoryRepository.flush();
    }
}

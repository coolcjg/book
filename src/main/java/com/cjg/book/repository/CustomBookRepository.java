package com.cjg.book.repository;

import com.cjg.book.domain.Book;
import com.cjg.book.dto.request.BookListRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBookRepository {
    Page<Book> list(Pageable pageable, BookListRequestDto dto);
}

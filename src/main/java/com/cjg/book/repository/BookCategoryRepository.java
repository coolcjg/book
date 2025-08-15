package com.cjg.book.repository;


import com.cjg.book.domain.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    List<BookCategory> findAllByBookBookId(long bookId);

    Long deleteAllByBookBookId(long bookId);
}

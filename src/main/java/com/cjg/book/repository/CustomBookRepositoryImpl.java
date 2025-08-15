package com.cjg.book.repository;

import com.cjg.book.code.CategoryCode;
import com.cjg.book.code.StatusCode;
import com.cjg.book.domain.Book;
import com.cjg.book.dto.request.BookListRequestDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.cjg.book.domain.QBook.book;
import static com.cjg.book.domain.QBookCategory.bookCategory;
import static com.cjg.book.domain.QCategory.category;


@Repository
@AllArgsConstructor
public class CustomBookRepositoryImpl implements CustomBookRepository {

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<Book> list(Pageable pageable, BookListRequestDto dto) {

        if(dto.getCategoryCodeList() == null){

            JPAQuery<Book> query =  jpaQueryFactory
                    .selectFrom(book)
                    .where(
                            eqStatus(dto.getStatusCode())
                            ,containsAuthor(dto.getAuthor())
                            ,containsName(dto.getName())
                    );

            JPAQuery<Long> countQuery = jpaQueryFactory
                    .select(book.count())
                    .from(book)
                    .where(
                            eqStatus(dto.getStatusCode())
                            ,containsAuthor(dto.getAuthor())
                            ,containsName(dto.getName())
                    );

            List<Book> list = query.orderBy(book.regDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            return PageableExecutionUtils.getPage(list , pageable, countQuery::fetchOne);
        }else{

            JPAQuery<Book> query =  jpaQueryFactory
                    .select(book).distinct()
                    .from(bookCategory)
                    .join(bookCategory.book, book)
                    .join(bookCategory.category, category)
                    .where(
                        inCategoryCode(dto.getCategoryCodeList())
                        ,eqStatus(dto.getStatusCode())
                        ,containsAuthor(dto.getAuthor())
                        ,containsName(dto.getName())
                    );

            JPAQuery<Long> countQuery = jpaQueryFactory
                    .select(book.countDistinct())
                    .from(bookCategory)
                    .join(bookCategory.book, book)
                    .join(bookCategory.category, category)
                    .where(
                        inCategoryCode(dto.getCategoryCodeList())
                        ,eqStatus(dto.getStatusCode())
                        ,containsAuthor(dto.getAuthor())
                        ,containsName(dto.getName())
                    );

            List<Book> list = query.orderBy(book.regDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            return PageableExecutionUtils.getPage(list , pageable, countQuery::fetchOne);
        }
    }

    private BooleanExpression eqStatus(StatusCode statusCode){
        if(statusCode==null) return null;
        return book.status.eq(statusCode.name());
    }

    private BooleanExpression containsAuthor(String author){
        if(!StringUtils.hasText(author)) return null;
        return book.author.contains(author);
    }

    private BooleanExpression containsName(String name){
        if(!StringUtils.hasText(name)) return null;
        return book.name.contains(name);
    }

    private BooleanExpression inCategoryCode(List<CategoryCode> list){
        if(list.isEmpty()) return null;
        return category.name.in(list.stream().map(Enum::name).toList());
    }
}

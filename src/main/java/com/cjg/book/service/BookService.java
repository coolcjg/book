package com.cjg.book.service;


import com.cjg.book.code.CategoryCode;
import com.cjg.book.code.ResultCode;
import com.cjg.book.code.StatusCode;
import com.cjg.book.domain.Book;
import com.cjg.book.domain.BookCategory;
import com.cjg.book.dto.request.BookDeleteRequestDto;
import com.cjg.book.dto.request.BookListRequestDto;
import com.cjg.book.dto.request.BookModifyRequestDto;
import com.cjg.book.dto.request.BookSaveRequestDto;
import com.cjg.book.dto.response.BookListResponseDto;
import com.cjg.book.dto.response.BookResponseDto;
import com.cjg.book.exception.CustomException;
import com.cjg.book.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookCategoryService bookCategoryService;
    private final CategoryService categoryService;
    private final BookRepository bookRepository;

    @Transactional
    public BookResponseDto save(BookSaveRequestDto bookDto){
        Book book = bookRepository.save(toBook(bookDto));

        for(CategoryCode code : bookDto.getCategoryCodeList()){
            bookCategoryService.save(BookCategory.builder().book(book).category(categoryService.findById(code.getCode())).build());
        }

        BookResponseDto bookResponseDto = toBookResponseDto(book);
        bookResponseDto.setCategoryCodeList(bookDto.getCategoryCodeList());
        return bookResponseDto;
    }

    public BookResponseDto findById(long bookId){
        BookResponseDto bookDto = toBookResponseDto(bookRepository.findById(bookId).orElseThrow(() -> new CustomException(ResultCode.BOOK_SEARCH_NOT_FOUND)));

        List<CategoryCode> categoryList = bookCategoryService.findAllByBookBookId(bookId)
                .stream()
                .map(e -> CategoryCode.valueOf(e.getCategory().getName()))
                .toList();

        bookDto.setCategoryCodeList(categoryList);
        return bookDto;
    }

    public BookListResponseDto list(BookListRequestDto bookListRequestDto){
        Pageable pageable = PageRequest.of(bookListRequestDto.getPageNumber()-1, bookListRequestDto.getPageSize(), Sort.Direction.DESC, "regDate");
        Page<Book> page =  bookRepository.list(pageable, bookListRequestDto);

        List<BookResponseDto> list = new ArrayList<>();
        for(Book book : page.getContent()) {
            BookResponseDto temp = BookResponseDto.builder()
                    .categoryCodeList(bookCategoryService.findAllByBookBookId(book.getBookId()).stream().map(e->CategoryCode.valueOf(e.getCategory().getName())).toList())
                    .bookId(book.getBookId())
                    .author(book.getAuthor())
                    .name(book.getName())
                    .statusCode(StatusCode.valueOf(book.getStatus()))
                    .regDate(book.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .modDate(book.getModDate() != null ? book.getModDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "")
                    .build();

            list.add(temp);
        }

        int totalPage = page.getTotalPages() == 0 ? 1 : page.getTotalPages();

        return BookListResponseDto.builder()
                        .bookList(list)
                        .pageNumber(page.getPageable().getPageNumber()+1)
                        .totalPage(totalPage)
                        .totalCount(page.getTotalElements())
                        .prevPage(getPageUrl(bookListRequestDto, "prev", totalPage))
                        .nextPage(getPageUrl(bookListRequestDto, "next", totalPage))
                        .build();
    }

    public String getPageUrl(BookListRequestDto dto, String direction, int totalPages){

        if( (direction.equals("prev") && dto.getPageNumber() == 1) || (direction.equals("next") && dto.getPageNumber() == totalPages) ){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("/v1/book/list?");

        if(dto.getCategoryCodeList() != null){
            sb.append("categoryCodeList=");
            sb.append(dto.getCategoryCodeList().toString()
                    .replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .replaceAll(" ", "")
            );
            sb.append("&");
        }

        if(dto.getStatusCode() != null){
            sb.append("statusCode=").append(dto.getStatusCode().name()).append("&");
        }

        if(dto.getAuthor() != null){
            sb.append("author=").append(dto.getAuthor()).append("&");
        }

        if(dto.getName() != null){
            sb.append("name=").append(dto.getName()).append("&");
        }

        if(direction.equals("prev")){
            sb.append("pageNumber=").append(dto.getPageNumber()-1).append("&");
            sb.append("pageSize=").append(dto.getPageSize()).append("&");
        }else{
            sb.append("pageNumber=").append(dto.getPageNumber()+1).append("&");
            sb.append("pageSize=").append(dto.getPageSize()).append("&");
        }

        if(sb.lastIndexOf("&") == sb.length()-1){
            sb.delete(sb.length()-1, sb.length());
        }

        return sb.toString();
    }

    @Transactional
    public BookResponseDto modify(BookModifyRequestDto bookModifyRequestDto){
        Book book = bookRepository.findById(bookModifyRequestDto.getBookId()).orElseThrow(() -> new CustomException(ResultCode.BOOK_SEARCH_NOT_FOUND));

        book.setStatus(bookModifyRequestDto.getStatusCode().name());
        book.setAuthor(bookModifyRequestDto.getAuthor());
        book.setName(bookModifyRequestDto.getName());
        book.setModDate(LocalDateTime.now());

        bookCategoryService.deleteAllByBookBookId(book.getBookId());
        bookCategoryService.flush();

        bookModifyRequestDto.getCategoryCodeList().forEach(
                e -> bookCategoryService.save(BookCategory.builder().book(book).category(categoryService.findById(e.getCode())).build())
        );

        BookResponseDto bookResponseDto = toBookResponseDto(book);
        bookResponseDto.setCategoryCodeList(bookModifyRequestDto.getCategoryCodeList());

        return bookResponseDto;
    }

    @Transactional
    public void delete(BookDeleteRequestDto bookDeleteRequestDto){

        bookCategoryService.deleteAllByBookBookId(bookDeleteRequestDto.getBookId());
        Book book = bookRepository.findById(bookDeleteRequestDto.getBookId()).orElseThrow(()-> new CustomException(ResultCode.BOOK_SEARCH_NOT_FOUND));
        bookRepository.delete(book);
    }

    public Book toBook(BookSaveRequestDto bookDto){
        return Book.builder()
                .author(bookDto.getAuthor())
                .name(bookDto.getName())
                .status(bookDto.getStatusCode().name())
                .build();
    }

    public BookResponseDto toBookResponseDto(Book book){
        return BookResponseDto.builder()
                .bookId(book.getBookId())
                .author(book.getAuthor())
                .name(book.getName())
                .statusCode(StatusCode.valueOf(book.getStatus()))
                .regDate(book.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .modDate(book.getModDate() != null ? book.getModDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "")
                .build();
    }
}

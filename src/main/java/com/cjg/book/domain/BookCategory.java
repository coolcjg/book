package com.cjg.book.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name="unique_bookId_categoryId", columnNames = {"BOOK_ID", "CATEGORY_ID"})
})
public class BookCategory {

    @Id
    @Column(name="BOOK_CATEGORY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookCategoryId;

    @ManyToOne
    @JoinColumn(name="BOOK_ID")
    private Book book;

    @ManyToOne
    @JoinColumn(name="CATEGORY_ID")
    private Category category;

}

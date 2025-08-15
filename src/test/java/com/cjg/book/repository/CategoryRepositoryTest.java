package com.cjg.book.repository;

import com.cjg.book.code.ResultCode;
import com.cjg.book.domain.Category;
import com.cjg.book.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

/*
@DataJpaTest : JPA관련 컴포넌트만 잘라서 테스트하기 위해 제공하는 테스트 어노테이션, Repository특화
@Import(TestConfig.class) : queryDSL관련 설정 추가
@AutoConfigureTestDatabase : 인메모리DB로 교체하는 기능 끄기
@TestPropertySource : DB프로퍼티 읽기 설정
 */

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application.properties")
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("Category 조회")
    public void findById(){
        Category category = categoryRepository.save(Category.builder().name("test").build());
        Category result = categoryRepository.findById(category.getCategoryId()).orElseThrow(() -> new CustomException(ResultCode.CATEGORY_SEARCH_NOT_FOUND));
        Assertions.assertThat(result.getName()).isEqualTo(category.getName());
    }

}
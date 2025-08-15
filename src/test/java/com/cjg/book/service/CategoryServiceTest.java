package com.cjg.book.service;

import com.cjg.book.code.CategoryCode;
import com.cjg.book.domain.Category;
import com.cjg.book.exception.CustomException;
import com.cjg.book.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

/*
@ExtendWith : Spring이 관리하는 컴포넌트에 모의객체(mock obejcts)를 자동으로 주입한다.
 */
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    /*
    @Mock
    특정 클래스의 Mock객체를 생성. 의존객체에 대해 사용한다. 실제 객체의 동작을시뮬레이션.

    @InjectMocks
    테스트 대상 객체를 생성하고, 이 객체의 의존성을 자동으로 주입. 테스트 대상 클래스의 실제 동작을 검증한다.
    */

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 조회 : 성공")
    public void findById_ok(){
        Long id = CategoryCode.cook.getCode();

        Category category = Category.builder().categoryId(CategoryCode.cook.getCode()).name(CategoryCode.cook.getName()).build();

        given(categoryRepository.findById(id)).willReturn(Optional.of(category));

        Category result = categoryService.findById(id);

        Assertions.assertThat(result.getCategoryId()).isEqualTo(category.getCategoryId());
        Assertions.assertThat(result.getName()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("카테고리 조회 : 실패 : 없는 카테고리")
    public void findById_fail_null(){
        Long id = 8L;
        given(categoryRepository.findById(id)).willReturn(Optional.empty());
        Assertions.assertThatThrownBy(()->categoryService.findById(id)).isInstanceOf(CustomException.class);
    }
}

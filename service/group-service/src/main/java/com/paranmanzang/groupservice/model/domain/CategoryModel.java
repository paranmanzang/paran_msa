package com.paranmanzang.groupservice.model.domain;

import com.paranmanzang.groupservice.model.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryModel {
    @NotBlank(message = "카테고리명은 필수값입니다.")
    private String name;

    public static CategoryModel fromEntity(Category category) {
        return CategoryModel.builder()
                .name(category.getName())
                .build();
    }
}
package com.example.core.category.mapper;

import com.example.core.category.db.CategoryEntity;
import com.example.rest.category.response.CategoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryEntity toEntity(CategoryResponseDto categoryResponse);

    CategoryResponseDto toDto(CategoryEntity categoryEntity);
}

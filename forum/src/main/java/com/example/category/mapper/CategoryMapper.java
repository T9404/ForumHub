package com.example.category.mapper;

import com.example.category.db.CategoryEntity;
import com.example.category.controller.response.CategoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryEntity toEntity(CategoryResponseDto categoryResponse);

    CategoryResponseDto toDto(CategoryEntity categoryEntity);
}

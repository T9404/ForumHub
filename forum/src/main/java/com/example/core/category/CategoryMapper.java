package com.example.core.category;

import com.example.public_interface.category.CategoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryEntity toEntity(CategoryResponseDto categoryResponse);

    CategoryResponseDto toDto(CategoryEntity categoryEntity);
}

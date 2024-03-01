package com.example.public_interface.category;

import com.example.core.category.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper( CategoryMapper.class );

    CategoryEntity toEntity(CategoryResponseDto categoryResponse);
}

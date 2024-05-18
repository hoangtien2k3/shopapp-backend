package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.dtos.CategoryDTO;
import com.hoangtien2k3.shopappbackend.models.Category;
import com.hoangtien2k3.shopappbackend.repositories.CategoryRepository;
import com.hoangtien2k3.shopappbackend.services.CategoryService;
import com.hoangtien2k3.shopappbackend.utils.LocalizationUtils;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public Category createCategory(CategoryDTO category) {
        Category newCategory = Category.builder()
                .name(category.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).
                orElseThrow(() -> new RuntimeException(localizationUtils.getLocalizedMessage(MessageKeys.NOT_FOUND)));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category oldCategory = getCategoryById(categoryId);
        oldCategory.setName(categoryDTO.getName());
        return categoryRepository.save(oldCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        // xoá cứng trong DB
        categoryRepository.deleteById(categoryId);
    }

}

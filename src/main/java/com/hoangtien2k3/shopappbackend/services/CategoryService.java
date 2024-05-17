package com.hoangtien2k3.shopappbackend.services;

import com.hoangtien2k3.shopappbackend.dtos.CategoryDTO;
import com.hoangtien2k3.shopappbackend.models.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDTO category);
    Category getCategoryById(Long categoryId);
    List<Category> getAllCategories();
    Category updateCategory(Long categoryId, CategoryDTO category);
    void deleteCategory(Long categoryId);
}

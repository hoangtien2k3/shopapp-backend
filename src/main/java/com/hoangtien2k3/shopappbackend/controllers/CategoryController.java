package com.hoangtien2k3.shopappbackend.controllers;

import com.hoangtien2k3.shopappbackend.components.TranslateMessages;
import com.hoangtien2k3.shopappbackend.components.converters.CategoryMessageConverter;
import com.hoangtien2k3.shopappbackend.dtos.CategoryDTO;
import com.hoangtien2k3.shopappbackend.models.Category;
import com.hoangtien2k3.shopappbackend.responses.ApiResponse;
import com.hoangtien2k3.shopappbackend.services.CategoryService;
import com.hoangtien2k3.shopappbackend.utils.Const;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController extends TranslateMessages {

    private final CategoryService categoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<?> createCategory(@RequestBody @Valid CategoryDTO categoryDTO,
                                            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessage = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        ApiResponse.builder()
                                .message(translate(MessageKeys.ERROR_MESSAGE))
                                .errors(errorMessage.stream()
                                        .map(this::translate)
                                        .toList()).build()
                );
            }
            Category newCategory = categoryService.createCategory(categoryDTO);

            // send kafka category
            this.kafkaTemplate.send(Const.KAFKA_TOPIC_INSERT_CATEGORY, newCategory); // producer
            this.kafkaTemplate.setMessageConverter(new CategoryMessageConverter());

            return ResponseEntity.ok(ApiResponse.builder().success(true)
                    .message(translate(MessageKeys.CREATE_CATEGORIES_SUCCESS))
                    .payload(newCategory)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .error(e.getMessage())
                    .message(translate(MessageKeys.CREATE_CATEGORIES_FAILED)).build());
        }
    }

    // ai cũng có thể lấy ra danh sách các danh mục sản phẩm
    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        /*kafka get all category*/
        this.kafkaTemplate.send(Const.KAFKA_TOPIC_GET_ALL_CATEGORY, categories);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .payload(categories)
                .build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategories(@PathVariable("id") Long id,
                                              @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(ApiResponse.builder().success(true)
                .message(translate(MessageKeys.UPDATE_CATEGORIES_SUCCESS))
                .payload(category)
                .build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(ApiResponse.builder().success(true)
                    .message(translate(MessageKeys.DELETE_CATEGORIES_SUCCESS))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .error(e.getMessage())
                    .message(translate(MessageKeys.DELETE_CATEGORIES_SUCCESS))
                    .build());
        }
    }
}

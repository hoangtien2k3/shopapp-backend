package com.hoangtien2k3.shopappbackend.controllers;

import com.github.javafaker.Faker;
import com.hoangtien2k3.shopappbackend.components.TranslateMessages;
import com.hoangtien2k3.shopappbackend.dtos.ProductDTO;
import com.hoangtien2k3.shopappbackend.dtos.ProductImageDTO;
import com.hoangtien2k3.shopappbackend.models.Product;
import com.hoangtien2k3.shopappbackend.models.ProductImage;
import com.hoangtien2k3.shopappbackend.responses.ApiResponse;
import com.hoangtien2k3.shopappbackend.responses.product.ProductListResponse;
import com.hoangtien2k3.shopappbackend.responses.product.ProductResponse;
import com.hoangtien2k3.shopappbackend.services.ProductService;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController extends TranslateMessages {

    private final ProductService productService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages.stream()
                        .map(this::translate)
                        .toList());
            }

            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(
                    ApiResponse.builder().success(true)
                            .message(translate(MessageKeys.CREATE_PRODUCT_SUCCESS))
                            .payload(newProduct)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .error(translate(MessageKeys.CREATE_PRODUCT_FAILED)).build());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(
            value = "/uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ) {
        try {
            Product existsProduct = productService.getProductById(productId);
            files = (files == null) ? new ArrayList<>() : files;
            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(ApiResponse.builder()
                        .error(translate(MessageKeys.FILES_REQUIRED)).build());
            }

            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }

                // Kiểm tra kích thước và định dạng file
                if (file.getSize() > 10 * 1024 * 1024) { // kích thước lớn hơn 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                            ApiResponse.builder().error(translate(MessageKeys.FILES_IMAGES_SIZE_FAILED)).build()
                    );
                }

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body(ApiResponse.builder().error(translate(MessageKeys.FILES_IMAGES_TYPE_FAILED)).build());
                }

                // lưu file và cập nhật thumnail trong DTO
                String fileName = storeFile(file);
                // lưu vào đối tượng product trong DB ->
                ProductImage productImage = productService.createProductImage(
                        existsProduct.getId(),
                        ProductImageDTO.builder().imageUrl(fileName).build()
                );
                productImages.add(productImage);
            }

            return ResponseEntity.ok(ApiResponse.builder().success(true)
                    .message(translate(MessageKeys.FILES_IMAGES_SUCCESS))
                    .payload(productImages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .message(translate(MessageKeys.FILES_IMAGES_FAILED))
                    .error(e.getMessage()).build());
        }
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    @GetMapping("/images/{image-name}")
    public ResponseEntity<?> viewImage(@PathVariable("image-name") String imageName) {
        try {
            Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource urlResource = new UrlResource(imagePath.toUri());

            if (urlResource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(urlResource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri().toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image file");
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID() + "_" + fileName;
        // đường dẫn đến thư mục mà bạn muốn lưu file
        Path uploadDir = Paths.get("uploads");
        // kiểm tra và tạo thư mục nêú nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // đường dẫn đầy đủ đến file
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // sao chép file vào thư mục
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    // hàm kiểm tra xem có đúng định dạng file ảnh hay không
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProduct(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit
    ) {
        // tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page,
                limit,
                Sort.by("id").ascending()
        );
        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);

        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                .products(products)
                .pageNumber(page)
                .totalElements(productPage.getTotalElements())
                .pageSize(productPage.getSize())
                .isLast(productPage.isLast())
                .totalPages(productPage.getTotalPages())
                .build());
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
        try {
            Product existsProducts = productService.getProductById(id);
            return ResponseEntity.ok(ApiResponse.builder().success(true)
                    .payload(ProductResponse.fromProduct(existsProducts)).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                            .message(translate(MessageKeys.GET_INFORMATION_FAILED))
                            .error(e.getMessage()).build()
            );
        }
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    @GetMapping("/details")
    public ResponseEntity<?> getProductDetailsById(@RequestParam("id") Long id) {
        try {
            Product existsProducts = productService.getDetailProducts(id);
            return ResponseEntity.ok(ApiResponse.builder().success(true).payload(existsProducts).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .message(translate(MessageKeys.GET_INFORMATION_FAILED))
                    .error(e.getMessage()).build());
        }
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        try {
            // tách ids thành mảng các số nguyên
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(ApiResponse.builder().success(true).payload(products).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .message(translate(MessageKeys.GET_INFORMATION_FAILED))
                    .error(e.getMessage()).build());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id,
                                           @RequestBody ProductDTO productDTO
    ) {
        try {
            Product updateProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(ApiResponse.builder().success(true).payload(updateProduct).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .message(translate(MessageKeys.MESSAGE_UPDATE_GET))
                    .error(e.getMessage()).build());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable("id") Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(ApiResponse.builder().success(true)
                    .message(translate(MessageKeys.MESSAGE_DELETE_SUCCESS, id)).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.builder()
                            .message(translate(MessageKeys.MESSAGE_DELETE_FAILED))
                            .error(e.getMessage()).build()
            );
        }
    }

    // fack dữ liệu
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/generate-faceker-products")
    public ResponseEntity<?> generateFacekerProducts() {
        Faker faker = new Faker(new Locale("vi")); // new Locale("en")
        for (int i = 0; i < 10000; i++) {
            String productName = faker.commerce().productName();
            if (productService.existsProduct(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float) faker.number().numberBetween(10, 90000000))
                    .description(faker.lorem().sentence())
                    .categoryId((long) faker.number().numberBetween(2, 7))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake product generated successfully");
    }

}

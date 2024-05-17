package com.hoangtien2k3.shopappbackend.mapper;

import com.hoangtien2k3.shopappbackend.dtos.ProductDTO;
import com.hoangtien2k3.shopappbackend.models.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductDTO productDTO);
}

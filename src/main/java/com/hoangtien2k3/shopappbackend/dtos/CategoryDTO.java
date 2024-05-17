package com.hoangtien2k3.shopappbackend.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryDTO {
    @NotEmpty(message = "Category's name can't be empty")
    private String name;
}

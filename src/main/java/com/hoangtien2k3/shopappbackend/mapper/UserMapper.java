package com.hoangtien2k3.shopappbackend.mapper;

import com.hoangtien2k3.shopappbackend.dtos.UserDTO;
import com.hoangtien2k3.shopappbackend.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDTO userDTO);

}

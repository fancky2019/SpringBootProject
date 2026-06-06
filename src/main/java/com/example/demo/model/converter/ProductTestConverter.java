package com.example.demo.model.converter;


import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.model.response.ProductTestResponse;
import org.mapstruct.Mapper;

import java.util.List;

// 关键：componentModel = "spring" 让生成的实现类成为 Spring Bean
@Mapper(componentModel = "spring")
public interface ProductTestConverter {

//    ProductTestResponse toDTO(ProductTest productTest);
//
//    User toEntity(UserDTO userDTO);


//    @Mapping(source = "userName", target = "username")  // 源字段→目标字段
//    @Mapping(source = "emailAddress", target = "email")
    ProductTestResponse toResponse(ProductTest productTest);
    List<ProductTestResponse> toResponseList(List<ProductTest> productTestList);  // 集合映射，自动生成

//    @Mapping(source = "username", target = "userName")
//    @Mapping(source = "email", target = "emailAddress")
    ProductTest toEntity(DemoProductRequest request);


}

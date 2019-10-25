package com.example.demo.dao.valvulas;

import com.example.demo.model.entity.valvulas.ValvulasProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValvulasProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ValvulasProduct record);

    int insertSelective(ValvulasProduct record);

    ValvulasProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ValvulasProduct record);

    int updateByPrimaryKey(ValvulasProduct record);

    List<ValvulasProduct> getProducts(ValvulasProduct product);
}
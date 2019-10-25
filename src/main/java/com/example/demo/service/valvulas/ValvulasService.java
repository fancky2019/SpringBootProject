package com.example.demo.service.valvulas;

import com.example.demo.dao.valvulas.ValvulasProductMapper;
import com.example.demo.dynamicDataSource.DataSourceAnnotation;
import com.example.demo.dynamicDataSource.DataSourceStrings;
import com.example.demo.model.entity.valvulas.ValvulasProduct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValvulasService {
    @Autowired
    ValvulasProductMapper productMapper;
    private static Logger logger = LogManager.getLogger(ValvulasService.class);


    @DataSourceAnnotation(DataSourceStrings.WRITER)
    public List<ValvulasProduct> getProductsWriter(ValvulasProduct product) {
        try {
            return productMapper.getProducts(product);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @DataSourceAnnotation(DataSourceStrings.READER)
    public List<ValvulasProduct> getProductsReader(ValvulasProduct product) {
        try {
            return productMapper.getProducts(product);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

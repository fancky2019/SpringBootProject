package com.example.demo.controller.valvulas;

import com.example.demo.model.entity.valvulas.ValvulasProduct;
import com.example.demo.service.valvulas.ValvulasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dynamicDataSource")
public class DynamicDataSourceController {

    Logger logger = LoggerFactory.getLogger(DynamicDataSourceController.class);

    @Resource
    private ValvulasService valvulasService;

    @RequestMapping("/getProductsWriter")
    @ResponseBody
    public List<ValvulasProduct> getProductsWriter(ValvulasProduct product) {
        List<ValvulasProduct> list = new ArrayList<>();
        try {
            list = valvulasService.getProductsWriter(product);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return list;
    }

    @RequestMapping("/getProductsReader")
    @ResponseBody
    public List<ValvulasProduct> getProductsReader(ValvulasProduct product) {
        List<ValvulasProduct> list = new ArrayList<>();
        try {
            list = valvulasService.getProductsReader(product);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return list;
    }
}

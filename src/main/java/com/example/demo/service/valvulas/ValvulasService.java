package com.example.demo.service.valvulas;

import com.example.demo.dao.valvulas.ValvulasProductMapper;
import com.example.demo.dynamicDataSource.DataSourceAnnotation;
import com.example.demo.dynamicDataSource.DataSourceStrings;
import com.example.demo.model.entity.valvulas.ValvulasProduct;

import com.example.demo.model.entity.wms.Product;
import com.example.demo.model.viewModel.MessageResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
service 层开启事务则不能动态切换数据源，
多数据源设计就不符合微服务的理念。
至于多个服务事务采用分布式事务管理


 通过加注解 @DataSourceAnnotation(DataSourceStrings.READER)
 反射动态切换数据源（读、写Server）
 */
@Service
public class ValvulasService {
    @Autowired
    ValvulasProductMapper productMapper;
    private static Logger logger = LogManager.getLogger(ValvulasService.class);


    //    @DataSourceAnnotation(DataSourceStrings.WRITER)
    public List<ValvulasProduct> getProductsWriter(ValvulasProduct product) {
        try {
            return productMapper.getProducts(product);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //    @DataSourceAnnotation(DataSourceStrings.READER)
    public List<ValvulasProduct> getProductsReader(ValvulasProduct product) {
        try {
            return productMapper.getProducts(product);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //    @DataSourceAnnotation
    @Transactional(rollbackFor = Exception.class)
    public MessageResult<Void> updateByPrimaryKey(ValvulasProduct record) throws Exception {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = productMapper.updateByPrimaryKey(record);
            messageResult.setSuccess(result > 0);
            //异常没有更新，事务回滚
//            Integer m=Integer.valueOf("dd");
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            throw ex;
        } finally {
            return messageResult;
        }
    }

}

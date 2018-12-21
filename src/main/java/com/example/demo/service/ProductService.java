package com.example.demo.service;

import com.example.demo.dao.ProductMapper;
import com.example.demo.model.entity.Product;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.ProductVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductMapper productMapper;

    //    public  int deleteByPrimaryKey(Integer id)
//    {
//        return 0;
//    }
//
//    public  int insert(Product record) {
//        return 0;
//    }
//
//    public  int insertSelective(Product record) {
//        return 0;
//    }
//
//    public  Product selectByPrimaryKey(Integer id) {
//        return null;
//    }
//
//    public int updateByPrimaryKeySelective(Product record) {
//        return 0;
//    }
//
//    public int updateByPrimaryKeyWithBLOBs(Product record) {
//        return 0;
//    }
//

    public MessageResult<Void> updateByPrimaryKey(Product record) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = productMapper.updateByPrimaryKey(record);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
        } finally {
            return messageResult;
        }
    }


    public List<Product> getProductProc(HashMap<String, Object> map) {
        try {
            return productMapper.getProductProc(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Product> getProductProc(Product Product) {
        try {
            return productMapper.getProductProc(Product);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ProductVM> getPageData(ProductVM viewModel) {
        try {
            return productMapper.getPageData(viewModel);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

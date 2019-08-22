package com.example.demo.service.wms;

import com.example.demo.dao.wms.ProductMapper;
import com.example.demo.model.entity.wms.Product;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import com.example.demo.service.rabc.UserManagerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductMapper productMapper;
    private static Logger logger = LogManager.getLogger(UserManagerService.class);
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

    //    @Async
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

    public MessageResult<PageData<ProductVM>> getPageData(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            List<ProductVM> list = productMapper.getPageData(viewModel);
            PageData<ProductVM> paegData = new PageData<>();
            paegData.setData(list);
            message.setData(paegData);
            message.setSuccess(true);
        } catch (Exception ex) {
            message.setSuccess(false);
            message.setMessage(ex.getMessage());
            logger.error(ex.toString());
        } finally {
            return message;
        }
    }


}

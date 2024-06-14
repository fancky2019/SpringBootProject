package com.example.demo.controller;

import com.example.demo.model.entity.wms.Product;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.pojo.Page;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.service.wms.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Resource
    private ProductService productService;


    //http://localhost:8081/product/getProductProc?price=10&productname=p
    @RequestMapping("/getProductProc")
    public List<Product> getProductProc(Product product) {

        //region 用HashMap
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("price", product.getPrice());
//        map.put("productname", product.getProductname());
//        List<Product> list = productService.getProductProc(map);
        //endregion

        //用对象
        List<Product> list = productService.getProductProc(product);
        return list;
    }

    @RequestMapping("/getPageData")
    public MessageResult<PageData<ProductVM>> getPageData(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            message = productService.getPageData(viewModel);
//            Thread.sleep(10*1000);
//            message = productService.getPageData(viewModel);//查看缓存问题
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    /**
     * @param product
     * @return
     */
    @PostMapping("/updateProduct")
    public MessageResult<Void> updateProduct(@RequestBody Product product) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = productService.updateByPrimaryKey(product);
        } catch (Exception e) {
            e.printStackTrace();
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }
}

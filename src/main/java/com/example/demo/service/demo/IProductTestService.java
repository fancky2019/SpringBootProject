package com.example.demo.service.demo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.viewModel.MessageResult;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2022-11-17
 */
public interface IProductTestService extends IService<ProductTest> {

    void mybatisPlusTest() throws InterruptedException;

     String getStringKey( int id) throws Exception ;

    }

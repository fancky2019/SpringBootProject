package com.example.demo.service.demo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.eventbus.MyCustomEvent;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.model.viewModel.MessageResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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

    String getStringKey(int id) throws Exception;

    void exportByPage(HttpServletResponse response, DemoProductRequest request) throws IOException, NoSuchFieldException, IllegalAccessException;

    void exportDemoProductTemplate(HttpServletResponse response) throws IOException;

    void importExcelProductTest(HttpServletResponse response, MultipartFile file) throws IOException;

    void updateBatchByIdTest();

    void coverUpdateTestOne(String name) throws InterruptedException;

    void coverUpdateTestTwo(String name);

    void deadLockOne() throws InterruptedException;

    void deadLockTwo() throws InterruptedException;

    void eventBusTest();

    void eventBusTest1();

    void repeatReadTest();

    void repeatReadFun();

    void transactionalFunTest();

    void transactionalFun();

    void transactionalFunRequiresNew();

    boolean batchUpdateByCondition();

    Integer batchUpdateBySelective() throws Exception;
}

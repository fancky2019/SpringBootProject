package com.example.demo.service.demo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.model.request.TestRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;

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

    void readSpecificCell(MultipartFile[] files, TestRequest testRequest) throws Exception;

    void saveBatchTest();

    void updateBatchByIdTest();

    void deleteBatchTest();

    void coverUpdateTestOne(String name) throws InterruptedException;

    void coverUpdateTestTwo(String name);

    void deadLockOne() throws InterruptedException;

    void deadLockTwo() throws InterruptedException;

    void eventBusTest() throws JsonProcessingException;

    void eventBusTest1();

    void repeatReadTest() throws InterruptedException;

    void repeatReadMultiTransaction() throws InterruptedException;

    void repeatReadMultiTransactionUpdate();


    void repeatReadFun();

    void transactionalFunTest();

    void transactionalFun();
    void transactionalFunAsyncWrapper();

    void noTranMethodCallTranMethod();

    void transactionalFunRequiresNew();

    boolean batchUpdateByCondition();

    Integer batchUpdateBySelective() throws Exception;

    void updateField() throws InterruptedException;

    void transactionalCallBack() throws Exception;

    void transactionalRedission(int i) throws InterruptedException;

    void transactionalRedissonForShare(BigInteger id) throws InterruptedException;

    void pointcutExecuteOrder();

    void tryThrowStackTrace();

    void mqMessageConsume(MqMessage message);

    Integer mqMessageOperation() throws JsonProcessingException;

    ProductTest getByIdForShareMySql(BigInteger id);

    ProductTest getByIdLastForShareMySql(BigInteger id);
}

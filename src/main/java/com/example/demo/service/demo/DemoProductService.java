package com.example.demo.service.demo;

import com.example.demo.dao.demo.DemoProductMapper;
import com.example.demo.dao.rabc.AuthoritiesMapper;
import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.service.rabc.AuthoritiesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DemoProductService {

    private static Logger logger = LogManager.getLogger(DemoProductService.class);
    @Autowired
    DemoProductMapper demoProductMapper;

    /*
      数据量过大，分批量插入：5K每次；1W条耗时1.3s左右
     */
    public int batchInsert() {
        List<DemoProduct> list = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            DemoProduct demoProduct = new DemoProduct();
            demoProduct.setGuid(UUID.randomUUID().toString());
            demoProduct.setProductName("productName" + i);
            demoProduct.setProductStyle("productStyle" + i);
            demoProduct.setImagePath("D:\\fancky\\git\\Doc");
            demoProduct.setCreateTime(LocalDateTime.now());
            demoProduct.setModifyTime(LocalDateTime.now());
            demoProduct.setStatus(Short.valueOf("1"));
            demoProduct.setDescription("setDescription_sdsdddddddddddddddd");
            demoProduct.setTimestamp(LocalDateTime.now());
            list.add(demoProduct);
        }

        StopWatch stopWatch = new StopWatch("BatchInsert");
        stopWatch.start("BatchInsert_Trace1");

        int i = demoProductMapper.batchInsert(list);
        stopWatch.stop();
//        stopWatch.start("BatchInsert_Trace2");
        long miils = stopWatch.getTotalTimeMillis();
        logger.info(stopWatch.shortSummary());

        return 0;
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.NOT_SUPPORTED)
    public int insert() {
        List<DemoProduct> list = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            DemoProduct demoProduct = new DemoProduct();
            demoProduct.setGuid(UUID.randomUUID().toString());
            demoProduct.setProductName("productName" + i);
            demoProduct.setProductStyle("productStyle" + i);
            demoProduct.setImagePath("D:\\fancky\\git\\Doc");
            demoProduct.setCreateTime(LocalDateTime.now());
            demoProduct.setModifyTime(LocalDateTime.now());
            demoProduct.setStatus(Short.valueOf("1"));
            demoProduct.setDescription("setDescription_sdsdddddddddddddddd");
            demoProduct.setTimestamp(LocalDateTime.now());
            list.add(demoProduct);
        }

        int i = demoProductMapper.batchInsert(list);

        int m = Integer.parseInt("m");
        return 0;
    }

}

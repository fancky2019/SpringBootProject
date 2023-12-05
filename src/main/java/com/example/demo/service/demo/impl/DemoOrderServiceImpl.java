package com.example.demo.service.demo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.demo.DemoOrderMapper;
import com.example.demo.model.entity.demo.DemoOrder;
import com.example.demo.service.demo.IDemoOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2023-11-30
 */
@Service
@Slf4j
public class DemoOrderServiceImpl extends ServiceImpl<DemoOrderMapper, DemoOrder> implements IDemoOrderService {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    public int batchInsertSession() {
        List<DemoOrder> list = new ArrayList<>();
        for (int i = 0; i < 2000000; i++) {
            DemoOrder demoOrder = new DemoOrder();
            demoOrder.setOrderNumber(i+"");
            demoOrder.setUserId(i);
            demoOrder.setAddress("上海市徐汇区漕河泾开发区");
            demoOrder.setAmount(new BigDecimal(i));
            demoOrder.setPayState(0);
            demoOrder.setCreateTime(LocalDateTime.now());
            demoOrder.setUpdateTime(LocalDateTime.now());

            list.add(demoOrder);
        }

        StopWatch stopWatch = new StopWatch("BatchInsert");
        stopWatch.start("BatchInsert_Trace1");

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        //不能用spring 注入的mapper,必须从session 里取，否则是一条一条插入
        DemoOrderMapper demoOrderMapper = sqlSession.getMapper(DemoOrderMapper.class);
        list.forEach(demoOrderMapper::insert);
//        sqlSession.clearCache();
        sqlSession.commit();

        stopWatch.stop();
//        stopWatch.start("BatchInsert_Trace2");
        long mills = stopWatch.getTotalTimeMillis();
        log.info("DemoOrder batchInsertSession {} ms",mills);
        return 0;
    }
}

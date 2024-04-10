package com.example.demo.service.demo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.demo.DemoOrderItemMapper;
import com.example.demo.model.entity.demo.DemoOrderItem;
import com.example.demo.service.demo.IDemoOrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2023-11-30
 */
@Service
@Slf4j
public class DemoOrderItemServiceImpl extends ServiceImpl<DemoOrderItemMapper, DemoOrderItem> implements IDemoOrderItemService {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    public int batchInsertSession() {
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime=LocalDateTime.parse("2010-07-02 00:00:00",dateTimeFormatter);

        List<DemoOrderItem> list = new ArrayList<>();
//        2000000
        for (int i = 0; i < 2; i++) {

            localDateTime=localDateTime.plusSeconds(200);
            DemoOrderItem demoOrderItem = new DemoOrderItem();
            demoOrderItem.setOrderId(i);
            demoOrderItem.setProductId(i);
            demoOrderItem.setSellerId(i);
            demoOrderItem.setCount(5);
            demoOrderItem.setPrice(BigDecimal.valueOf(i));
            demoOrderItem.setStatus(1);
            demoOrderItem.setCreateTime(localDateTime);
            demoOrderItem.setUpdateTime(LocalDateTime.now());

            list.add(demoOrderItem);
        }

        StopWatch stopWatch = new StopWatch("DemoOrderItemBatchInsert");
        stopWatch.start("DemoOrderItemBatchInsert_Trace1");

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            //MybatisMapperProxy
            //不能用spring 注入的mapper,必须从session 里取，否则是一条一条插入
            //mapper:MybatisMapperProxy 内部newInstance 调用jdk 动态代理生成mapper 代理类 ,
            // invoke 内部调用，
            // 实现接口MapperMethodInvoker 的Invoke
            //内部 MybatisMapperMethod
            //DynamicContext  的 DynamicSqlSource  的 getBoundSql 拼接生成sql语句
            DemoOrderItemMapper demoOrderItemMapper = sqlSession.getMapper(DemoOrderItemMapper.class);
            list.forEach(demoOrderItemMapper::insert);
//        sqlSession.clearCache();
            sqlSession.commit();
        } catch (Exception ex) {
            log.error("", ex);
        }
        stopWatch.stop();
//        stopWatch.start("BatchInsert_Trace2");
        long mills = stopWatch.getTotalTimeMillis();
        log.info("DemoOrderItem batchInsertSession {} ms", mills);

        return 0;
    }
}

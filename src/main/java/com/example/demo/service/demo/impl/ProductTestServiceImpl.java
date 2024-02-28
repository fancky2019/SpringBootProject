package com.example.demo.service.demo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.demo.ProductTestMapper;
import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.service.demo.IProductTestService;
import com.example.demo.utility.ConfigConst;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2022-11-17
 */
@Service
@Slf4j
public class ProductTestServiceImpl extends ServiceImpl<ProductTestMapper, ProductTest> implements IProductTestService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ProductTestMapper productTestMapper;

    @Autowired
    private ObjectMapper objectMapper;

    public ProductTestServiceImpl(ProductTestMapper productTestMapper) {
        this.productTestMapper = productTestMapper;
    }


    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void mybatisPlusTest() throws InterruptedException {

//        this.baseMapper.deleteBatchIds();
//        this.saveEntity();
//        saveOrUpdateBatch();
//        queryById();
//        queryTest();
//        queryParam();
//        truncateTest();
//        deleteTableDataTest();
//        selectMaxId();

        /*
        mybatis  缓存默认 一级缓存 sqlSession 级别 ，二级缓存 mapper 级别
        spring mybatis 缓存需要在事务内开启，否则无效。

        下面两次查询如果开启事务只会执行一次数据库查询，不开启事务会执行两次数据库查询
         */
        ProductTest productTest = this.getById(17);

        Thread.sleep(5000);
        ProductTest productTest1 = this.getById(17);
    }

    private void saveEntity() {
        ProductTest productTest = new ProductTest();
        productTest.setGuid(UUID.randomUUID().toString());
        productTest.setProductName("productName");
        productTest.setProductStyle("productStyle");
        productTest.setImagePath("D:\\fancky\\git\\Doc");
        productTest.setCreateTime(LocalDateTime.now());
        productTest.setModifyTime(LocalDateTime.now());
        productTest.setStatus(1);
        productTest.setDescription("setDescription_sdsdddddddddddddddd");
        productTest.setTimestamp(LocalDateTime.now());
        this.save(productTest);

    }

    private void saveOrUpdateBatch() {
        List<ProductTest> productTests = new ArrayList<ProductTest>();
        ProductTest productTest = new ProductTest();
        productTest.setId(new BigInteger("100001"));
        productTest.setGuid("dssddssd");
        productTests.add(productTest);

        productTest = new ProductTest();
        productTest.setId(new BigInteger("100002"));
        productTest.setGuid("sdqwe43");
        productTests.add(productTest);

//        this.saveOrUpdateBatch(productTests);
        //更新
        this.updateBatchById(productTests);
        //保存
//        this.saveBatch()；
        this.save(productTest);
        this.removeById(productTest);
        this.updateById(productTest);
//        this.listByIds();
        QueryWrapper<ProductTest> queryWrapper = new QueryWrapper<ProductTest>();
//        queryWrapper.select("","");
//        queryWrapper.eq("",1);
//        queryWrapper.ne();
        this.list(queryWrapper);
    }

    private void queryParam() {
        LambdaQueryWrapper<ProductTest> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductTest::getProductName, "productName_xiugai55555");
        lambdaQueryWrapper.last("limit 3");
        //分页
        List<ProductTest> list1 = this.list(lambdaQueryWrapper);
        int m = 0;
    }

    private void queryById() {
        ProductTest list1 = this.getById(17);
        int m = 0;
    }

    private void queryTest() {
        /*
        原符号       <       <=      >       >=      <>
        对应函数    lt()     le()    gt()    ge()    ne()
        Mybatis-plus写法：  queryWrapper.ge("create_time", localDateTime);
        Mybatis写法：       where create_time >= #{localDateTime}


        //不加last(“desc”)默认就是升序，加上是降序方式

        List<Employee> list=empployeeMapper.selecList(
        new EntityWrapper<Employee> ()
        .eq("gender",0)
        .orderBy("age")
        .last("desc")
        );
         */


        QueryWrapper<ProductTest> queryWrapper = new QueryWrapper<ProductTest>();
//        queryWrapper.select("","");
//        queryWrapper.eq("",1);
//        queryWrapper.ne();
        String productName = "";
        //有条件拼接条件
        queryWrapper.eq(StringUtils.isNotEmpty(productName), "product_name", productName);
//        queryWrapper.orderByDesc("desc");
        List<ProductTest> list = this.list(queryWrapper);

        LambdaQueryWrapper<ProductTest> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductTest::getProductName, "productName_xiugai55555");
        lambdaQueryWrapper.last("limit 3");
        List<ProductTest> list1 = this.list(lambdaQueryWrapper);

        LambdaUpdateWrapper<MqMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(MqMessage::getPublishAck, true);
//        updateWrapper.eq(MqMessage::getMsgId, msgId);//条件
//        mqMessageService.update(updateWrapper);


        ProductTestMapper productTestMapper = this.getBaseMapper();
        //链式查询方式
        List<ProductTest> list2 = new LambdaQueryChainWrapper<ProductTest>(this.getBaseMapper())
                .eq(ProductTest::getProductName, "productName_xiugai55555")
                .list();
        int m = 0;
    }

    /*
    1.truncate先使用create命令创建表，然后drop源表，最后rename新表。
    2 drop只是删除元数据，所以比delete快很多，特别是大表
    3 truncate本质是ddl，需要ddl权限。ddl本身是自提交的，所以truncate也不能rollback回滚
    4 因为是truncate是重建表，所以truncate是可以整理表碎片的（delete不可以）
    5 truncate在执行有外键约束的reference表时会失败

     */

    /**
     * truncate 截断表是不会回滚的
     * truncate 会删除所有数据，并且不记录日志，不可以恢复数据，相当于保留了表结构
     */
    @Transactional(rollbackFor = Exception.class)
    public void truncateTest() {
        this.productTestMapper.truncateTest();
        int m = Integer.parseInt("m");
    }

    private void deleteTableDataTest() {
        //DELETE FROM product_test
        LambdaQueryWrapper<ProductTest> queryWrapper = new LambdaQueryWrapper<>();
        this.remove(queryWrapper);
        int m = 0;
    }

    private void selectMaxId() {
        //注：不是LambdaQueryWrapper
        QueryWrapper<ProductTest> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("max(id) id");
        ProductTest productTest = this.getOne(queryWrapper);
        //查不到为null
        if (productTest != null) {
            BigInteger maxId = productTest.getId();
        }
        int m = 0;
    }

    /*
    更新表的指定字段
     */
    private void updateField() {
        UpdateWrapper<ProductTest> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("ID", 0);//条件
        updateWrapper.set("SESSION_KEY", "abc");//要更新的列
        //  实体要指定null ，不然默认更新非空字段
        baseMapper.update(null, updateWrapper);


        //根据条件删除
//        QueryWrapper<User> wrapper = new QueryWrapper<>();
//        wrapper.eq("user","zyh3"); //通过wrapper设置条件
//        baseMapper.delete(wrapper);
//        service.remove(wrapper);


//MyBatis-Plus updateById方法更新不了空字符串/null解决方法
//        userService.update(null,new UpdateWrapper<User>().lambda()
//                .set(User::getUserName,null)
//                .eq(User::getUserId,user.getUserId()));

    }

    //region redis

    /**
     * * 雪崩：随机过期时间
     * * 击穿：分布式锁（表名），没有取到锁，sleep(50)+重试
     * * 穿透：分布式锁（表名）+设置一段时间的null值，没有取到锁，sleep(50)+重试
     *
     * @param id
     * @return
     * @throws Exception
     */
    public String getStringKey(@PathVariable int id) throws Exception {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = ConfigConst.DEMO_PRODUCT_PREFIX + id;
        String val = valueOperations.get(key);
        if (StringUtils.isEmpty(val)) {

            String lockKey = ConfigConst.DEMO_PRODUCT_PREFIX + "redisson";
            //获取分布式锁，此处单体应用可用 synchronized，分布式就用redisson 锁
            RLock lock = redissonClient.getLock(lockKey);
            try {

                boolean lockSuccessfully = lock.tryLock(30, 60, TimeUnit.SECONDS);
                if (!lockSuccessfully) {
                    log.info("Thread - {} 获得锁 {}失败！锁被占用！", Thread.currentThread().getId(), lockKey);
                    return null;
                }
                BigInteger idB = BigInteger.valueOf(id);
                ProductTest productTest = this.baseMapper.getById(idB);
                //穿透：设置个空值
                if (productTest == null) {
                    valueOperations.set(key, ConfigConst.EMPTY_VALUE);
                    redisTemplate.expire(key, 60, TimeUnit.SECONDS);
                } else {
                    String json = objectMapper.writeValueAsString(productTest);
                    valueOperations.set(key, json);
                }
            } catch (Exception e) {
                throw e;
            } finally {
                //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
                //unlock 删除key
                //如果锁因超时（leaseTime）会抛异常
                lock.unlock();
            }


        } else {
            if (ConfigConst.EMPTY_VALUE.equals(val)) {
                return null;
            }
        }


        return val;
    }
    //endregion

}

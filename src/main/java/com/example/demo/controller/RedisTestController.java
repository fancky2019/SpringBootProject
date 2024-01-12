package com.example.demo.controller;

import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.pojo.Student;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.service.demo.IProductTestService;
import com.example.demo.utility.ConfigConst;
import com.example.demo.utility.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 数据类型的首字母找对应的数据类型的操作
 * 操作命令中文文档：http://www.redis.cn/commands/lpushx.html
 * https://redis.io/commands/
 * <p>
 * 密码配置： SECURITY配置节点 ，requirepass fancky123456
 * <p>
 * redis集群最少三台主三从。
 * <p>
 * RedLock；集群配置最少三台机器，最好为奇数。(N/2 + 1)中成功获取锁，则获取锁成功。
 * redisson在加锁的时候，key=lockName, value=uuid + threadID, 采用set结构存储，
 * 并包含了上锁的次数 （支持可重入）；
 * 解锁的时候通过hexists判断key和value是否存在，存在则解锁；这里不会出现误解锁
 * <p>
 * 持久化：rdb,aof。默认RDB,如果不丢就用aof方式。
 * <p>
 * <p>
 * 主从同步：主从同步刚连接的时候进行全量同步；全量同步结束后开始增量同步。如果有需要，slave在任何时候都可以发起全量同步，
 * 其主要策略就是无论如何首先会尝试进行增量同步，如果步成功，则会要求slave进行全量同步，之后再进行增量同步。
 * 只要slave启动，就会和master建立连接发送SYNC请求和主机全量同步。
 * <p>
 * <p>
 * redis 高可用：redis 主从、redis sentinel、 redis cluster 。
 * <p>
 * redis cluster :解决sentinel扩容问题。集群分片存储。每个节点都有自己的至少一个从节点，
 * 若有一个节点的主从都宕机，集群就不可用。每个节点保存其他节点的主从信息，主节点不可用就切换从节点。
 * <p>
 * 雪崩：随机过期时间
 * 击穿：分布式锁（表名），没有取到锁，sleep(50)+重试
 * 穿透：分布式锁（表名）+设置一段时间的null值，没有取到锁，sleep(50)+重试
 * <p>
 * redis key 过期订阅：
 */

/**
 * 指定前缀的key放在一个文件夹下。如：key_sb:UserInfo:1 路径前缀之间用冒号分开，当key超过两个RedisDesktop会显示在一个文件夹下。
 * <p>
 * redis key 过期订阅：
 */
@RestController
@RequestMapping("/redisTest")
public class RedisTestController {
    private static final Logger logger = LogManager.getLogger(RedisTestController.class);

    //如果定义了泛型，实例化时没有指明类的泛型，则认为此泛型类型为Object
    //相当于 RedisTemplate<Object,Object>
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IProductTestService productTestService;
    /**
     * 要配置redis 此种类型的bean.
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplateObj;

//    @Autowired
//    private RedisTemplate<String,Integer> redisTemplateInt;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisUtil redisUtil;

    //region redis basic operation
    @GetMapping("")
    public MessageResult<Void> redisTest() {

        try {

            //key 存在
            redisTemplate.hasKey("ds");
            redisTemplate.expire("ds", 60, TimeUnit.SECONDS);
            //  redisTemplate.getExpire()
            //region String
            //待测试
            ValueOperations<Integer, Integer> valueOperations1 = redisTemplate.opsForValue();

            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set("stringKey1", "stringKeyValue1");
            valueOperations.set("stringKey2", "stringKeyValue2");
            valueOperations.set("stringKey3", "stringKeyValue3");
            //取值
            String strVal = valueOperations.get("stringKey1");
            String strVal3 = valueOperations.get("stringKey3", 0, -1);
            //删除
            strVal3 = "stringKey3";
            Boolean delByStringKey = redisTemplate.delete("stringKey1");
            Boolean del = redisTemplate.delete(strVal3);
            Boolean exists = redisTemplate.hasKey(strVal3);

            redisTemplate.expire("stringKey1", 1000, TimeUnit.SECONDS);
            //endregion

            //region List
            ListOperations<String, String> listOperations = redisTemplate.opsForList();
            listOperations.leftPush("listKey1", "listKeyValue1");
            listOperations.leftPush("listKey2", "listKeyValue2");
            listOperations.leftPush("listKey3", "listKeyValue3");
            listOperations.leftPush("listKey4", "listKeyValue4");
            //取值
            String listVal4 = listOperations.leftPop("listKey1");
            //删除
            String listVal = listOperations.leftPop("listKey1");
            redisTemplate.delete("listKey2");
            //endregion

            //region Hash
            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            hashOperations.put("hashKey1", "hashField1", "hashValue1");
            hashOperations.put("hashKey1", "hashField12", "hashValue12");
            hashOperations.put("hashKey1", "hashField13", "hashValue13");
            hashOperations.put("hashKey2", "hashField2", "hashValue2");
            hashOperations.put("hashKey3", "hashField3", "hashValue3");

            //取值
            String hashVal1 = hashOperations.get("hashKey1", "hashField1");
            //删除
            //  hashOperations.delete("hashKey1", new String[]{"hashField1","hashField12"});
            hashOperations.delete("hashKey1", "hashField1", "hashField12");
            redisTemplate.delete("hashKey2");
            //endregion

            //region Set
            SetOperations<String, String> setOperations = redisTemplate.opsForSet();
            setOperations.add("setKey1", "setValue1,setValue2,setValue3,setValue4");
            setOperations.add("setKey2", "setValue2");
            setOperations.add("setKey3", "setValue3");
            setOperations.add("setKey4", "setValue4");
            //取值
            Set<String> setStr = setOperations.members("setKey1");
            Iterator<String> stringIterator = setStr.iterator();
            while (stringIterator.hasNext()) {
                String str = stringIterator.next();
                Integer n = 0;
            }
            //删除
            //可删除
//            setOperations.remove("setKey1", "setValue1,setValue2,setValue3,setValue4");
            //删除不掉
//            setOperations.remove("setKey1", "setValue1,setValue2");
            redisTemplate.delete("setKey2");
            //endregion


            //region 数据结构ZSet--有序集合 SortedSet----zipList 、skipList
            ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
            zSetOperations.add("zSetKey1", "zSetValue1", 1);
            zSetOperations.add("zSetKey1", "zSetValue11", 11);
            zSetOperations.add("zSetKey1", "zSetValue12", 12);
            zSetOperations.add("zSetKey1", "zSetValue13", 13);
            zSetOperations.add("zSetKey2", "zSetValue2", 4);
            zSetOperations.add("zSetKey3", "zSetValue3", 3);
            zSetOperations.add("zSetKey4", "zSetValue4", 2);
            //获取
            //取所有
            Set<String> zSetStr = zSetOperations.range("zSetKey1", 0, -1);
            //取三个
            Set<String> zSetStr1 = zSetOperations.range("zSetKey1", 0, 2);
            for (String str : zSetStr) {
                Integer n = 0;
            }
            //删除
            redisTemplate.delete("zSetKey1");
            //endregion

            //region SortedSet

        } catch (Exception ex) {
            String msg = ex.toString();
            Integer m = 0;
        }
        return null;
    }

    @GetMapping("/object")
    public MessageResult<Void> redisObjectTest() {

        try {


            ValueOperations<String, ProductTest> productTestValueOperations = redisTemplate.opsForValue();
            ProductTest productTest = productTestService.getById(100003);
            productTestValueOperations.set("productTest:" + productTest.getId(), productTest);
            Student student1 = new Student();
            student1.setName("fancky");
            student1.setAge(25);
            Student student2 = new Student();
            student2.setName("li");
            student2.setAge(26);
            Student student3 = new Student();
            student3.setName("rui");
            student3.setAge(27);

            //region String

            // 数据结构
            //StringRedisKey1  StringValue1
            //StringRedisKey1  StringValue2
            //StringRedisKey1  StringValue3
            //    *                *
            //    *                *
            //    *                *
            ValueOperations<String, Student> valueOperations = redisTemplate.opsForValue();
            valueOperations.set("stringKey1", student1);
            valueOperations.set("stringKey2", student2);
            valueOperations.set("stringKey3", student3);
            //取值
            Student strVal = valueOperations.get("stringKey1");

            //删除
            String strKey3 = "stringKey3";
            Boolean delByStringKey = redisTemplate.delete("stringKey2");
            Boolean del = redisTemplate.delete(strKey3);
            Boolean exists = redisTemplate.hasKey(strKey3);
            //endregion

            //region List

            //数据结构
            //ListRedisKey1   ListValue1
            //                ListValue2
            //                ListValue3
            //                    *
            //                    *
            //                    *
            //ListRedisKey2   ListValue1
            //                ListValue2
            //                ListValue3
            //                    *
            //                    *
            //                    *
            ListOperations<String, Student> listOperations = redisTemplate.opsForList();
            listOperations.leftPush("listKey1", student1);
            listOperations.leftPush("listKey1", student3);
            listOperations.leftPush("listKey2", student1);
            listOperations.leftPush("listKey2", student2);
            listOperations.leftPush("listKey2", student3);
            listOperations.leftPush("listKey3", student3);


            //取值--根据key 取出该key 所有值 list 集合
            List<Student> listVal3 = listOperations.range("listKey1", 0, -1);
            //出队一个并删除  --返回出队的值
            Student listVal = listOperations.leftPop("listKey1");
            //删除key
            redisTemplate.delete("listKey2");
            //endregion

            //region Hash

            // 数据结构
            //HashSetRedisKey1  HashSetKey1 HashSetValue1
            //                  HashSetKey2 HashSetValu2
            //                  HashSetKey3 HashSetValu3
            //                       *            *
            //                       *            *
            //                       *            *
            //HashSetRedisKey2  HashSetKey1 HashSetValue1
            //                  HashSetKey2 HashSetValu2
            //                  HashSetKey3 HashSetValu3
            //                      *             *
            //                      *             *
            //                      *             *
            HashOperations<String, String, Student> hashOperations = redisTemplate.opsForHash();
            hashOperations.put("hashKey1", "hashField1", student1);
            hashOperations.put("hashKey1", "hashField2", student2);
            hashOperations.put("hashKey1", "hashField3", student3);
            hashOperations.put("hashKey2", "hashField1", student1);
            hashOperations.put("hashKey2", "hashField2", student2);
            hashOperations.put("hashKey3", "hashField3", student3);

            //取值
            Student hashVal1 = hashOperations.get("hashKey1", "hashField1");

            //  //获取redis key 下的所有hash key 的hash 对应jredis 的hKeys(rawKey);
            Set<String> keys = hashOperations.keys("hashKey3");
            Iterator<String> iter = keys.iterator();
            while (iter.hasNext()) {
                String key = iter.next();
            }

            for (String val : keys) {
                String v = val;
            }

            List<String> hashKeysList = new ArrayList<>();
            hashKeysList.add("hashField3");
            List<Student> studentList1 = hashOperations.multiGet("hashKey1", hashKeysList);

            HashMap<String, Student> hashMap = new HashMap<>(16);
            hashMap.put("hashField1", student1);
            hashMap.put("hashField2", student2);
            hashOperations.putAll("hashKey1", hashMap);

            boolean has = hashOperations.hasKey("hashKey1", "hashField1");

            //删除
            //  hashOperations.delete("hashKey1", new String[]{"hashField1","hashField12"});
            //返回删除的个数
            Long deleteCount = hashOperations.delete("hashKey1", "hashField1", "hashField12");
            redisTemplate.delete("hashKey2");
            //endregion

            //region Set

            //数据结构
            //SetRedisKey1    SetValue1
            //                SetValue2
            //                SetValue3
            //                    *
            //                    *
            //                    *
            //SetRedisKey2    SetValue1
            //                SetValue2
            //                SetValue3
            //                    *
            //                    *
            //                    *

            SetOperations<String, Student> setOperations = redisTemplate.opsForSet();
            setOperations.add("setKey1", student1, student2, student3);
            setOperations.add("setKey2", student1, student2);
            setOperations.add("setKey3", student3);
            //HashSet--Set
            //取值
            Set<Student> setStr = setOperations.members("setKey1");
            List<Student> studentList = new ArrayList<>();

            Iterator<Student> stringIterator = setStr.iterator();
            while (stringIterator.hasNext()) {
                Student str = stringIterator.next();
                Integer n = 0;
            }

            //删除

            //删除集合中的一个值
            setOperations.remove("setKey1", student2);

            redisTemplate.delete("setKey2");
            //endregion


            //region 数据结构ZSet--有序集合 SortedSet----zipList 、skipList

            //数据结构
            //                                           Score
            //SortedSetRedisKey1    SortedSetValue1        1
            //                      SortedSetValue2        2
            //                      SortedSetValue3        3
            //                           *                 *
            //                           *
            //                           *
            //SortedSetRedisKey2    SortedSetValue1        1
            //                      SortedSetValue2        2
            //                      SortedSetValue3        3
            //                           *                 *
            //                           *                 *
            //                           *                 *


            /*
            sorted set有两种实现方式，一种是ziplist压缩表，一种是zset(dict、skiplist)，redis.conf有两个配置来控制，
            当sorted set中的元素个数小于128时(即元素对member score的个数，共256个元素)，使用ziplist，
            当元素对中member长度超过64个字节时使用zset。

            zset配置
            zset-max-ziplist-entries	128
            zset-max-ziplist-value	64
————————————————
             */
//            DefaultZSetOperations
//            ZSetOperations<String, Student> zSetOperations1 = redisTemplate.opsForZSet();
            ZSetOperations<String, Student> zSetOperations1 = redisTemplate.opsForZSet();
            zSetOperations1.add("zSetKey1", student1, 1);
            zSetOperations1.add("zSetKey1", student2, 11);
            zSetOperations1.add("zSetKey1", student3, 12);
            zSetOperations1.add("zSetKey2", student1, 4);
            zSetOperations1.add("zSetKey2", student2, 4);
            zSetOperations1.add("zSetKey3", student1, 3);

            //获取
            //取所有
            Set<Student> zSetStr = zSetOperations1.range("zSetKey1", 0, -1);
            //取三个
            Set<Student> zSetStr1 = zSetOperations1.range("zSetKey1", 0, 2);
            studentList = new ArrayList<>(zSetStr1);
            Iterator<Student> zSetStringIterator = zSetStr.iterator();
            while (zSetStringIterator.hasNext()) {
                Student str = zSetStringIterator.next();
                Integer n = 0;
            }

            //删除
            //删除指定Key中的值
            zSetOperations1.remove("zSetKey1", student2, student3);
            //当key中的值都删除，就把key也删除
            zSetOperations1.remove("zSetKey2", student1, student2);

            redisTemplate.delete("zSetKey1");
            //endregion


            redisTemplate.opsForValue().set("stringKey", "value");

            int id1 = 1, id2 = 2;
            redisTemplate.opsForValue().setBit("bitMapKey1", id1, true);
            redisTemplate.opsForValue().setBit("bitMapKey2", id2, false);

        } catch (Exception ex) {
            String msg = ex.toString();
            Integer m = 0;
        }
        return null;
    }

    //endregion

    //region Redisson
    //配置集群的时候;@SpringBootApplication(exclude = {RedissonAutoConfiguration.class})

    /**
     * 单Redis节点模式
     * doc:https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95
     * <p>
     * 使用redisson 的配置没有成功。让redisson采用springboot的redis的配置
     */
    @RequestMapping("/testRedisson")
    public String testRedisson() throws InterruptedException {
        //获取分布式锁，只要锁的名字一样，就是同一把锁
        RLock lock = redissonClient.getLock("redisKey_testRedisson");

        //使用默认看门狗延期机制    this.lockWatchdogTimeout = 30000L;
        // lock.lock();
        //加锁（阻塞等待），默认过期时间是30秒。   this.lockWatchdogTimeout = 30000L;
//        lock.lock();
        try {
            boolean isLocked = lock.isLocked();
            if (isLocked) {
                logger.error(MessageFormat.format("Thread - {0} 获得锁失败！锁被占用！", Thread.currentThread().getId()));
            }
            //500MS 获取锁，3000锁维持时间
            //内部采用信号量控制等待时间  Semaphore
            //    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit)
            //注：waitTime 设置时间长一点
            //写入hash类型数据：redisKey:lock hashKey  uuid:线程id  hashValue:thread id
            boolean lockSuccessfully = lock.tryLock(1, 30, TimeUnit.SECONDS);
//            lock.lock();
//            lock.lock(10, TimeUnit.SECONDS);
            //或者直接返回
            isLocked = lock.isLocked();

            if (lockSuccessfully) {
                logger.info(MessageFormat.format("Thread - {0} 获得锁！", Thread.currentThread().getId()));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
            //unlock 删除key
            lock.unlock();
        }

        return "Hello Redisson!";
    }
    //endregion

    /**
     * redis 穿透 击穿
     *
     * @param id
     * @return
     */
    @GetMapping("/getStringKey/{id}")
    public MessageResult<String> getStringKey(@PathVariable int id) throws Exception {
        String val = productTestService.getStringKey(id);
        return MessageResult.success(val);
    }
}

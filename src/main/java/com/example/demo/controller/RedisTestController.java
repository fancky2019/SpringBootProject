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

import org.checkerframework.checker.units.qual.min;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 数据类型的首字母找对应的数据类型的操作
 * 操作命令中文文档：http://www.redis.cn/commands/lpushx.html
 * https://redis.io/commands/
 *
 * 密码配置： SECURITY配置节点 ，requirepass fancky123456
 *
 * redis集群最少三台主三从。
 *
 * RedLock；集群配置最少三台机器，最好为奇数。(N/2 + 1)中成功获取锁，则获取锁成功。
 * redisson在加锁的时候，key=lockName, value=uuid + threadID, 采用set结构存储，
 * 并包含了上锁的次数 （支持可重入）；
 * 解锁的时候通过hexists判断key和value是否存在，存在则解锁；这里不会出现误解锁
 *
 * 持久化：rdb,aof。默认RDB,如果不丢就用aof方式。
 *
 *
 * 主从同步：主从同步刚连接的时候进行全量同步；全量同步结束后开始增量同步。如果有需要，slave在任何时候都可以发起全量同步，
 * 其主要策略就是无论如何首先会尝试进行增量同步，如果步成功，则会要求slave进行全量同步，之后再进行增量同步。
 * 只要slave启动，就会和master建立连接发送SYNC请求和主机全量同步。
 *
 *
 * redis 高可用：redis 主从、redis sentinel、 redis cluster 。
 *
 * 在 Redis 集群中，默认，主节点负责读写操作，而从节点主要用于数据备份和故障转移。这种模式被称为 主读主写（主节点同时处理读和写请求）。
 *
 * redis cluster :解决sentinel扩容问题。集群分片存储。每个节点都有自己的至少一个从节点，
 * 若有一个节点的主从都宕机，集群就不可用。每个节点保存其他节点的主从信息，主节点不可用就切换从节点。
 *
 * 雪崩：随机过期时间
 * 击穿：分布式锁（表名），没有取到锁，sleep(50)+重试
 * 穿透：分布式锁（表名）+设置一段时间的null值，没有取到锁，sleep(50)+重试
 *
 * redis key 过期订阅：
 */

/**
 * 指定前缀的key放在一个文件夹下。如：key_sb:UserInfo:1 路径前缀之间用冒号分开，当key超过两个RedisDesktop会显示在一个文件夹下。
 *
 * redis key 过期订阅：
 *
 *
 *
 *
 *
 * spring boot 2的spring-boot-starter-data-redis中，默认使用的是lettuce作为redis客户端，它与jedis的主要区别如下：
 *
 * Jedis是同步的，不支持异步，Jedis客户端实例不是线程安全的，需要每个线程一个Jedis实例，所以一般通过连接池来使用Jedis
 * Lettuce是基于Netty框架的事件驱动的Redis客户端，其方法调用是异步的，Lettuce的API也是线程安全的，所以多个线程可以操作单个Lettuce连接来完成各种操作，同时Lettuce也支持连接池
 * 如果不使用默认的lettuce，使用jedis的话，可以排除lettuce的依赖，手动加入jedis依赖，配置如下
 *
 *
 *  Jedis：采用的是直连，就是直接去连接数据库，如果有多个线程操作的话就是不安全的，想要避免不安全，就需要使用Jedis pool连接池。
 *     Lettuce：底层采用的Netty，异步请求，实例可以在多个线程中共享，所以不存在线程不安全的情况，可以减少线程数量，就不需要再开连接池
 *
 *
 *redis  客户端对key进行 CRC16 计算，确定hash slot
 * key 的 CRC16 算法:输出结果：一个 16 位的无符号整数（范围：0 到 65535）
 * CRC16 计算完成后，客户端需要对结果取模，以确定键所属的哈希槽：slot = CRC16(key) % 16384
 *
 * Redis Cluster 使用 CRC16(key) % 16384 算法计算 key 的哈希槽（slot），Redis 共有 16384 个哈希槽，每个节点负责一定范围的槽位
 *
 *
 *
 * 在 Redis 集群中，客户端需要对键进行 CRC16 算法 计算，以确定该键所属的哈希槽（hash slot）。
 * Redis 集群共有 16384 个哈希槽，每个键通过 CRC16 计算后，再对 16384 取模，得到具体的槽位编号。
 *
 *
 * redis 客户端：redis_desktop_manager
 * Spring Boot 项目中，默认的 Redis 客户端是 Lettuce。Lettuce 是一个高性能的、基于 Netty 的 Redis 客户端，支持同步、异步和响应式编程模型。
 */


// region  Redis 集群默认主读主写
/*在 Redis 集群中，默认情况下，主节点负责读写操作，而从节点主要用于数据备份和故障转移。这种模式被称为 主读主写（主节点同时处理读和写请求）。

主节点写入
客户端将写请求发送到负责对应哈希槽的主节点。
主节点接收到写请求后，会先将数据写入本地内存。
如果写入成功，主节点会返回 OK 给客户端。





3. 主读主写的实现
以下是 Redis 集群主读主写的具体实现过程：

写入过程
客户端向主节点发送写请求。
主节点执行写操作，并将数据写入本地。
主节点将写操作异步复制到所有从节点。
主节点返回写操作结果给客户端。

读取过程
客户端向主节点发送读请求。
主节点执行读操作，并返回结果给客户端。

4. 从节点读取（可选）
虽然默认情况下从节点不处理客户端请求，但在某些场景下，可以通过以下方式从从节点读取数据：

启用从节点读取
客户端连接到从节点后，发送 READONLY 命令，使从节点进入只读模式。
从节点在只读模式下可以处理读请求，但不会处理写请求。

使用场景
读多写少：在读多写少的场景中，可以将部分读请求分流到从节点，减轻主节点的负载。
最终一致性：从节点的数据可能会有延迟（异步复制），因此适用于对一致性要求不高的场景。

注意事项
数据延迟：从节点的数据可能不是最新的，因为主从复制是异步的。
负载均衡：需要客户端或代理层实现从节点的负载均衡。



*/


// endregion

//region 集群脑裂解决、主从同步确认、写入确认、保证写入成功
/* 集群脑裂解决：配置，强一致性使用zk
   min-slaves-to-write：与主节点通信的从节点数量必须大于等于该值主节点，否则主节点拒绝写入。
   min-slaves-max-lag：主节点与从节点通信的ACK消息延迟必须小于该值，否则主节点拒绝写入。

 Redis 提供了一种 ACK 机制，允许客户端在写操作后等待从节点的确认，以确保数据已经成功复制到指定数量的从节点。
这种机制通过 WAIT 命令实现。
WAIT 命令的作用
WAIT 命令会阻塞客户端，直到指定数量的从节点确认已经接收到写操作。

示例：注意：两个命令，不是原子的要使用lua脚本实现
SET key value
WAIT 2 1000

lua脚本
-- 设置键值对
redis.call('SET', KEYS[1], ARGV[1])
-- 等待至少一个从节点确认
return redis.call('WAIT', tonumber(ARGV[2]), tonumber(ARGV[3]))

WAIT numreplicas timeout
numreplicas：需要等待的从节点数量。
timeout：超时时间（单位为毫秒 ms）。如果在超时时间内未收到足够数量的从节点确认，命令会返回已确认的从节点数量。

WAIT 命令的工作原理
客户端向主节点发送写操作（如 SET）。
主节点执行写操作，并将数据写入内存。
主节点将写操作异步复制到所有从节点。
客户端调用 WAIT 命令，阻塞并等待从节点的确认。
主节点收到指定数量的从节点确认后，返回成功响应给客户端。
如果在超时时间内未收到足够数量的从节点确认，WAIT 命令会返回已确认的从节点数量。

集群高可用：
 主节点写入、主从复制、写入确认机制 和 故障转移 来保证数据写入成功和主从一致性：
主节点写入	主节点负责处理写请求，并将数据写入本地内存。
主从复制	主节点将写操作异步复制到从节点，确保数据冗余。
写入确认	使用 WAIT 命令确保写操作同步到指定数量的从节点。
故障转移	主节点故障时，从节点选举为新的主节点，接管哈希槽并继续提供服务。
复制偏移量	主节点和从节点维护复制偏移量，确保数据同步进度。
部分同步	从节点断开连接后重新连接时，主节点发送丢失的写操作，避免全量同步。
全量同步	如果从节点落后太多，主节点触发全量同步，生成 RDB 文件并发送给从节点。
*/
//endregion


//region 持久化
/*
1. 持久化机制
Redis 提供了两种持久化方式，将内存中的数据保存到磁盘中，以防止数据丢失：

1.1 RDB（Redis Database Backup）
RDB 是 Redis 的快照持久化机制。它会定期将内存中的数据生成一个二进制文件（dump.rdb），保存到磁盘中。

触发条件：
手动执行 SAVE 或 BGSAVE 命令。
根据配置文件中的规则自动触发（如 save 900 1 表示 900 秒内至少有一个键被修改）。

优点：
文件紧凑，适合备份和恢复。
恢复速度快。

缺点：
数据可能会丢失（从最后一次快照到故障之间的数据）。
频繁的快照会影响性能。

配置示例：

bash
复制
save 900 1       # 900 秒内至少 1 个键被修改时触发
save 300 10      # 300 秒内至少 10 个键被修改时触发
save 60 10000    # 60 秒内至少 10000 个键被修改时触发
1.2 AOF（Append-Only File）
AOF 是 Redis 的日志持久化机制。它会将每个写操作记录到一个日志文件中（appendonly.aof），并在重启时通过重放日志来恢复数据。
触发条件：
每次写操作都会追加到 AOF 文件中（取决于配置的同步策略）。

同步策略：
appendfsync always：每次写操作都同步到磁盘，数据最安全，但性能最差。
appendfsync everysec：每秒同步一次，性能和安全性平衡（默认配置）。
appendfsync no：由操作系统决定何时同步，性能最好，但数据可能丢失。

优点：
数据丢失较少（取决于同步策略）。
日志文件可读性强，适合调试。
缺点：
文件体积较大，恢复速度较慢。
频繁写入可能影响性能。

配置示例：
appendonly yes              # 开启 AOF
appendfilename "appendonly.aof"  # AOF 文件名
appendfsync everysec        # 每秒同步一次

1.3 RDB + AOF 结合使用
为了兼顾性能和数据安全性，可以同时开启 RDB 和 AOF：
RDB 用于定期备份和快速恢复。
AOF 用于记录每次写操作，减少数据丢失。
配置示例：

save 900 1       # 开启 RDB
appendonly yes   # 开启 AOF
appendfsync everysec
 */
//endregion

// region Redis集群数据写入具体过程

/*
 *1. 客户端发起写入请求
 * 客户端向 Redis 集群发送写入请求，例如：SET mykey myvalue
 *
 * 2. 计算键的哈希槽
 * 客户端对键（mykey）进行 CRC16 算法 计算，确定其所属的哈希槽（hash slot）：
 * slot = CRC16("mykey") % 16384
 * Redis 集群共有 16384 个哈希槽，每个槽由集群中的一个节点负责。
 * 客户端通常会缓存槽位与节点的映射关系。
 *
 *3. 定位负责的节点
 * 客户端根据哈希槽（slot）找到负责该槽的节点：
 * 如果客户端已经缓存了槽位与节点的映射关系，则直接向目标节点发送请求。
 * 如果客户端没有缓存或缓存过期，客户端会随机连接集群中的一个节点，发送请求。
 *
 *4. 请求发送到目标节点
 * 客户端将写入请求发送到负责该哈希槽的节点。
 *
 *5. 节点处理写入请求
 * 目标节点接收到写入请求后，会执行以下操作：
 * 检查键的哈希槽：
 * 如果键的哈希槽由当前节点负责，则直接执行写入操作。
 * 如果键的哈希槽不由当前节点负责，节点会返回 MOVED 错误，并告知客户端正确的节点地址。
 * 执行写入操作：
 * 如果键的哈希槽由当前节点负责，节点会执行写入操作（如 SET、HSET 等）。
 * 写入成功后，节点会返回成功响应给客户端。
 *
 *6. 处理 MOVED 重定向
 * 如果客户端发送请求到错误的节点，节点会返回 MOVED 错误：
 * MOVED <slot> <node-ip>:<node-port>
 * <slot>：键所属的哈希槽。
 * <node-ip>:<node-port>：负责该槽的节点地址。
 * 客户端收到 MOVED 错误后，会更新本地缓存的槽位映射关系，并重新向正确的节点发送请求。
 *
 * 7. 处理 ASK 重定向
 * 在集群进行数据迁移（resharding）时，可能会出现 ASK 重定向：
 * ASK <slot> <node-ip>:<node-port>
 * <slot>：键所属的哈希槽。
 * <node-ip>:<node-port>：临时负责该槽的节点地址。
 * 客户端收到 ASK 错误后，会向临时节点发送 ASKING 命令，然后重新发送写入请求。
 *
 * 8. 数据同步（主从复制）
 * Redis 集群中的每个主节点都有一个或多个从节点。写入操作在主节点完成后，会异步复制到从节点：
 * 主节点将写入操作记录到本地。
 * 主节点将写入操作发送给所有从节点。
 * 从节点接收并执行写入操作，保持与主节点的数据一致性。
 *
 * 9. 返回结果给客户端
 * 目标节点执行完写入操作后，会将结果返回给客户端：
 * 如果写入成功，返回 OK。
 * 如果写入失败，返回错误信息。
 *
 * 10. 客户端处理响应
 * 客户端接收到节点的响应后，会根据结果进行相应处理：
 * 如果写入成功，客户端继续后续操作。
 * 如果收到 MOVED 或 ASK 错误，客户端会更新缓存并重试请求。
 *
 *
 *
 *
 */
//endregion

//region Redis集群数据读取具体过程
/*
 * 在 Redis 集群中，数据读取的过程与写入类似，但更加简单，因为读取操作不涉及数据同步和迁移。以下是
 * Redis 集群数据读取的具体过程：
 *
 * 1. 客户端发起读取请求
 * 客户端向 Redis 集群发送读取请求，例如：GET mykey
 *
 * 2. 计算键的哈希槽
 * 客户端对键（mykey）进行 CRC16 算法 计算，确定其所属的哈希槽（hash slot）：
 * slot = CRC16("mykey") % 16384
 * Redis 集群共有 16384 个哈希槽，每个槽由集群中的一个节点负责。
 * 客户端通常会缓存槽位与节点的映射关系。
 *
 * 3. 定位负责的节点
 * 客户端根据哈希槽（slot）找到负责该槽的节点：
 * 如果客户端已经缓存了槽位与节点的映射关系，则直接向目标节点发送请求。
 * 如果客户端没有缓存或缓存过期，客户端会随机连接集群中的一个节点，发送请求。
 *
 * 4. 请求发送到目标节点
 * 客户端将读取请求发送到负责该哈希槽的节点。
 *
 * 5. 节点处理读取请求
 * 目标节点接收到读取请求后，会执行以下操作：
 * 检查键的哈希槽：
 * 如果键的哈希槽由当前节点负责，则直接执行读取操作。
 * 如果键的哈希槽不由当前节点负责，节点会返回 MOVED 错误，并告知客户端正确的节点地址。
 * 执行读取操作：
 * 如果键的哈希槽由当前节点负责，节点会执行读取操作（如 GET、HGET 等）。
 * 读取成功后，节点会返回数据给客户端。
 *
 * 6. 处理 MOVED 重定向
 * 如果客户端发送请求到错误的节点，节点会返回 MOVED 错误：
 * MOVED <slot> <node-ip>:<node-port>
 * <slot>：键所属的哈希槽。
 * <node-ip>:<node-port>：负责该槽的节点地址。
 * 客户端收到 MOVED 错误后，会更新本地缓存的槽位映射关系，并重新向正确的节点发送请求。
 *
 * 7. 处理 ASK 重定向
 * 在集群进行数据迁移（resharding）时，可能会出现 ASK 重定向：
 * ASK <slot> <node-ip>:<node-port>
 * <slot>：键所属的哈希槽。
 * <node-ip>:<node-port>：临时负责该槽的节点地址。
 * 客户端收到 ASK 错误后，会向临时节点发送 ASKING 命令，然后重新发送读取请求。
 *
 * 8. 返回结果给客户端
 * 目标节点执行完读取操作后，会将结果返回给客户端：
 * 如果键存在，返回对应的值。
 * 如果键不存在，返回 nil。
 *
 * 9. 客户端处理响应
 * 客户端接收到节点的响应后，会根据结果进行相应处理：
 * 如果读取成功，客户端继续后续操作。
 * 如果收到 MOVED 或 ASK 错误，客户端会更新缓存并重试请求。
 *
 * 10. 从节点读取（可选）
 * 在某些场景下，客户端可以从从节点读取数据，以减轻主节点的负载：
 * 客户端需要显式指定从节点读取（例如使用 READONLY 命令）。
 * 从节点的数据可能会稍有延迟（异步复制），因此需要根据业务需求权衡一致性和性能。
 *
 */
//endregion

@RestController
@RequestMapping("/redisTest")
public class RedisTestController {
    private static final Logger logger = LogManager.getLogger(RedisTestController.class);

    //如果定义了泛型，实例化时没有指明类的泛型，则认为此泛型类型为Object
    //相当于 RedisTemplate<Object,Object>
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplateType;


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
     * redis 缓存穿透 缓存击穿
     *
     * @param id
     * @return
     */
    @GetMapping("/getStringKey/{id}")
    public MessageResult<String> getStringKey(@PathVariable int id) throws Exception {
        String val = productTestService.getStringKey(id);
        return MessageResult.success(val);
    }

    /**
     * redis 本身是缓存用，此处要求强一致性当做数据库来用，应用关系数据库较为合适
     * lua副本同步和aof 的always性能判断
     *
     *
     *redis 集群ack ，集群写入确认 ，lua 脚本
     * @param key key
     * @param value 值
     * @param numReplicas 写入成功的副本数
     * @param timeoutMillisecond 写入副本的超时时间 单位ms
     * @return
     */
    @GetMapping("/setAndWait")
    public MessageResult<String> setAndWait(String key, String value, int numReplicas, long timeoutMillisecond) {

       /* 注意：没有分号结尾
       -- KEYS[1]: 键名
-- ARGV[1]: 值
-- ARGV[2]: 需要等待的从节点数量
-- ARGV[3]: 超时时间（毫秒）

-- 写入数据
redis.call('SET', KEYS[1], ARGV[1])

-- 等待数据复制到从节点
local replicated = redis.call('WAIT', tonumber(ARGV[2]), tonumber(ARGV[3]))

-- 判断是否成功
if replicated >= tonumber(ARGV[2]) then
    return 1 -- 成功
else
    -- 如果未达到要求的从节点数量，删除已写入的数据
    redis.call('DEL', KEYS[1])
    return 0 -- 失败
end
        */

        //没有启动副本，numReplicas =1，返回0.key 写入redis 成功
      //  String scriptText = "redis.call('SET', KEYS[1], ARGV[1]); return redis.call('WAIT', tonumber(ARGV[2]), tonumber(ARGV[3]));";

        String scriptText = ""
                + "redis.call('SET', KEYS[1], ARGV[1]); "
                + "local replicated = redis.call('WAIT', tonumber(ARGV[2]), tonumber(ARGV[3])); "
                + "if replicated >= tonumber(ARGV[2]) then "
                + "    return 1; "
                + "else "
                + "    redis.call('DEL', KEYS[1]); "
                + "    return 0; "
                + "end";


        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(scriptText);
        script.setResultType(Long.class);


        Long result =redisTemplateType.<Long>execute(script, Collections.singletonList(key), value, String.valueOf(numReplicas), String.valueOf(timeoutMillisecond));
       // Long result1 =redisTemplateType.execute(script, Collections.singletonList(key), value, String.valueOf(numReplicas), String.valueOf(timeoutMillisecond));

        redisTemplate.opsForValue().set("stringKey", "value");

        return MessageResult.success();

    }
}

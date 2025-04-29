package com.example.demo.architecturedesign;

/*
前端高可用的组合:LVS+Keepalived、Nginx+Keepalived、HAproxy+Keepalived
 */
public class Design {


    //region 高可用集群设计
    /*
    nginx: keepalived + haproxy
    服务：nginx 反向代理
    redis: lua 脚本实现写入至少有一个副本写入
    rabbitMq:仲裁队列（不能实现水平扩容问题）取代镜像队列
    mysql:keepalived(vip)+半同步复制（不要全同步复制性能差）+GTID
    */

    //endregion




    /*
    lvs :LVS（Linux Virtual Server）：LVS是基于Linux操作系统的负载均衡软件，它通过网络地址转换（NAT）
         或直接路由（DR）的方式将请求分发到后端服务器群集。LVS使用IP负载均衡技术，可以根据不同的负载均衡算法（如轮询、加权轮询、源IP哈希等）
         将请求分发给后端服务器，并支持实现高可用性和故障恢复。
   F5:硬件负载均衡，作用于网络层，请求转发到对应服务机器
   nginx:应用层负载均衡
    */

    //region 主备
    /*
    nginx 反向代理 backup
    keepalived 主备 vip 漂移  。解决单点故障
     */
    //endregion

    //region  nginx HA

    /*
    热备方案：中间件， Keepalived、nginx、roseHA
    客户端和服务端加一个中间件，中间负责和服务通信，转发客户端和服务端通信，
    相当于把nginx的负载均衡去掉了，但是session要共享，比如放在redis中.
    可以自己实现一个这样的中间件。中间件用keepalived活nginx做高可用.

    keepalived 主备
    集群：

    */

    //region  Keepalived
    /*
    VRRP的出现是为了解决静态路由的单点故障。
    Keepalived高可用设计。VRRP全称 Virtual Router Redundancy Protocol，即 虚拟路由冗余协议。
    虚拟ip 漂移


    //keepalived 通过检测服务器的mysql 服务判断可用不可用关闭 mysql 服务
//#!/bin/bash
//# 检查 MySQL 服务是否正常运行
//if systemctl is-active --quiet mysqld; then
//    exit 0  # MySQL 正常，返回 0
//            else
//    exit 1  # MySQL 异常，返回 1
//    fi


    //region 工作流程
    3. Keepalived 的工作流程
    3.1 初始化
    每个节点启动 Keepalived 服务，读取配置文件。
    根据配置的优先级和虚拟路由器 ID，参与 VRRP 选举。

    3.2 主节点工作
    主节点定期发送 VRRP 通告消息，告知备份节点自己的状态。
    主节点绑定 VIP 并开始处理数据流量。
    主节点执行健康检查脚本，监控服务的状态。

    3.3 故障检测与切换
    如果主节点的健康检查失败，Keepalived 会降低主节点的优先级。
    备份节点检测到主节点的优先级降低或未收到通告消息，触发选举机制。
    优先级最高的备份节点成为新的主节点，并绑定 VIP。

     3.4 恢复
    当原主节点恢复后，Keepalived 会重新参与选举。
    如果原主节点的优先级高于当前主节点，VIP 会切换回原主节点。
   //endregion





    每台机器都装keepalived、haproxy ,他们只能用于linux服务器
    Keepalived 保证nginx 可用，nginx 动静分离，负载均衡
    主备：主干活，备不干活，资源浪费，可以用主备模式。session redis.
    互为主备：主备都干活。每个机器配置两个vrrp_instance, keepalived 是通过虚拟ip (vip)访问的，两个虚拟ip设置互为主备
    windows 自带的NLB 代替linux 的keepalived ，每台机器装一个keepalived，nginx 互为主备

    访问虚拟IP:keepalived 配置虚拟ip地址，虚拟ip地址在和nginx关联，就可以访问nginx.  几台Nginx 配置一样。 两台互为主备的Keepalived根据网络情况路由访问哪一台机器的nginx
             执行脚本判断nginx服务是否存在，不存在就杀掉keepalived进程。
    Keepalive 三大组件中的check 组件，监控nginx进程的脚本，如果nginx进程挂了，没有重启成功，keepalived自己停止服务，这样keepalived集群就知道应用状态。

   keepalived 配置 ： 虚拟地址-->虚拟服务器地址-->真是服务器地址
   nginx keepalived 配置参考
     */



/*
 集群高可用设计：
 每台服务器安装 Keepalived 和 haproxy nginx
 keepalive 解决haproxy的单点故障问题。保证haproxy的高可用。
 haproxy反向代理nginx实现负载均衡。haproxy一般非web服务器。
 nginx web服务器动静分离。

 客户端直接访问haproxy,haproxy反向代理到nginx,nginx反向代理到真正web服务器。

 部署参考链接
 https://blog.csdn.net/zhou641694375/article/details/127549434?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-127549434-blog-127729911.235%5Ev40%5Epc_relevant_anti_vip&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-127549434-blog-127729911.235%5Ev40%5Epc_relevant_anti_vip&utm_relevant_index=2

 HAProxy 用作LB（负载均衡）反向代理， 不能以web server 的方式工作。
 可作web server 的是Apache 或Nginx。
 HAProxy 可安装在其前端提供提供负载均衡和高可用。

web 服务器：Apache nginx tomcat iis
          同时使用apache和nginx，静态网页有nginx处理，动态交由apache处理，


          Tomcat是一个Java Servlet容器，可以运行Java Web应用程序
 */




    /*
    软件
    LVS、Haproxy、Nginx
    硬件
    F5

   LVS（Linux Virtual Server）即Linux虚拟服务器，

  吞吐量：tomcat<nginx <lvs < F5
  nginx 吞吐量 5--10W
     */
//endregion

    //region  Keepalived使用
    /*
    notify_master：在Keepalived状态切换到Master时执行指定的脚本

notify_backup：在Keepalived状态切换到backup时执行指定的脚本

notify_fault：在Keepalived检测到故障时执行指定的脚本

notify：在任何状态变化（Master、Backup或Fault）时执行指定的脚本

notify_stop：在Keepalived服务挂掉时执行指定的脚本，kill或者stop等停止keepalived的命令有效


 notify_master "/path/to/your/script.sh INSTANCE MASTER"
    notify_backup "/path/to/your/script.sh INSTANCE BACKUP"
    notify_fault "/path/to/your/script.sh INSTANCE FAULT"

#!/bin/bash

# Keepalived 传递的参数：
# $1 = "GROUP" 或 "INSTANCE"
# $2 = "MASTER" 或 "BACKUP" 或 "FAULT"
# $3 = 优先级（可选）

CONFIG_FILE="/path/to/your/springboot/application.yml"  # Spring Boot 配置文件路径
ACTUATOR_URL="http://localhost:8080/actuator/refresh"  # Spring Boot Actuator 刷新配置的URL
NEW_MYSQL_HOST="new_mysql_host"  # 新的 MySQL 主机地址
NEW_MYSQL_PORT="3306"            # 新的 MySQL 端口
NEW_MYSQL_USER="root"            # 新的 MySQL 用户名
NEW_MYSQL_PASSWORD="password"    # 新的 MySQL 密码

# 根据 Keepalived 状态执行操作
case "$2" in
    "MASTER")
        echo "切换为 MASTER 状态，更新 MySQL 配置..."
        # 更新 Spring Boot 配置文件中的 MySQL 连接信息
        sed -i "s/^\(spring.datasource.url: jdbc:mysql://\).*:\([0-9]*\/.*\)$/\1${NEW_MYSQL_HOST}:${NEW_MYSQL_PORT}\/\2/" $CONFIG_FILE
        sed -i "s/^\(spring.datasource.username: \).*$/\1${NEW_MYSQL_USER}/" $CONFIG_FILE
        sed -i "s/^\(spring.datasource.password: \).*$/\1${NEW_MYSQL_PASSWORD}/" $CONFIG_FILE

        # 调用 Spring Boot Actuator 刷新配置
        curl -X POST $ACTUATOR_URL
        ;;
    "BACKUP" | "FAULT")
        echo "切换为 BACKUP 或 FAULT 状态，无需更新配置。"
        ;;
    *)
        echo "未知状态: $2"
        exit 1
        ;;
esac

exit 0





     */
    //endregion

    //region  nginx
/*
upstream blance {#配置服务器的分别对应的应用ip和的端口
   #当前的server暂时不参与负载均衡
   server 192.168.3.11:8001 down;
   #预留的备份服务器
   server 192.168.3.11:8002 backup;
   #允许请求失败1次，失败后服务暂停10秒
   server 192.168.3.11:8003 max_fails=1 fail_timeout=10s;
}

因为8001端口的server1服务设置的down，不参与负载均衡； （服务不可用）
而8002端口的server2服务设置的backup，（备份）当其他节点服务正常时，不对外提供服务，当其他节点服务挂掉之后才会自动启用此备份服务；
所以只能访问到8003端口的server3应用服务（可用）
 */

//endregion

    //endregion

    //region rpc mq
    /*

    主要的区别：mq异步场景
              rpc是远程同步调用,立即获得结果
    mq 有broker 概念，负责存储消息，消息中间件
    rpc 就是一个远程函数调用，点对点通信
    消息队列是系统级、模块级的通信。RPC是对象级、函数级通信。
    */

    //endregion




    //region mysql ha
//    无法做到严格的主从同步，都会有延迟，如果强制从主库读就违反了，读写分离原则。如果将写入数据同步到redis 缓存，加大复杂性
    /*
     主主互为主从+keepalived :
     主主+keepalived  masterA和masterB互为主从，keepalived vip保证A写，B同步A，B和slave之间同步，slave会有延迟
     两台机器都装keepalived 、mysq,  java通过vip访问mysql 。两台mysql 互为主从

      MHA :会尝试保存故障主库的 binlog，但无法保证 100% 数据一致性。建议结合半同步复制使用。
     主从：master--- keepalive--vip-- mysqlA 和mysqlB
          slave---keepalive--vip-- mysqlC mysqlD mysqlE
     */

    /*
    mysql  mgr 高可用
     mysql 分片：用分库来实现

    高可用：中间件
     ProxySQL
     MySQL Router ：高可用建议通过Percona XtraDB Cluster或MariaDB Galera或MySQL官方的group replication实现，
                   如果实在没有选择，还可以通过MHA实现。
     */

     /*
      mysql主从同步：建议半同步复制+GTID
      MySQL主从复制默认异步复制进行同步。
      MySQL主从复制的原理：同步复制、异步复制（默认）、半同步复制、并行复制
      全同步复制（组复制 5.7支持）：配置、当master节点写数据的时候，会等待所有的slave节点完成数据的复制，然后才继续往下进行；组复制的每一个节点都可能是slave
      异步复制（默认）：主库 提交不关心从库是否提交、
      半同步复制：主库在执行完事务后，会等待至少一个从库接收并写入 Relay Log 后，才返回结果给客户端、
                需要安装插件并启用。只保证中继日志复制到从库，不保证日志被写入mysql .需要结合GTID或MHA保证数据一致性
      并行复制：



      GTID 复制：（GTID-Based Replication）： MySQL 5.6 引入的特性，为每个事务分配一个全局唯一的标识符
                      。从库通过 GTID 来追踪主库的事务，而不是通过二进制日志的文件名和位置。
                  流程：
                  1. 主库生成 GTID 并记录事务
                事务提交：
                当主库上执行一个事务时，MySQL 会为该事务生成一个全局唯一的 GTID。
                GTID 的格式为：source_id:transaction_id，其中：
                source_id 是主库的唯一标识（通常是 server_uuid）。
                transaction_id 是事务的唯一标识，从 1 开始递增。
                记录到二进制日志：
                主库将事务的 GTID 和事务内容记录到二进制日志（Binary Log）中。
                二进制日志中会包含 GTID 事件（GTID_LOG_EVENT），用于标识事务的 GTID。

                返回结果给客户端：
                主库在事务提交后，立即返回结果给客户端（如果是异步复制）。
                如果是半同步复制，主库会等待至少一个从库确认接收事务后，再返回结果。

                2. 从库获取 GTID 并应用事务
                从库连接主库：
                从库通过配置的复制用户连接到主库，并请求获取二进制日志。
                读取 GTID 事件：
                从库从主库的二进制日志中读取 GTID 事件，获取事务的 GTID 和事务内容。
                从库会记录已经接收到的 GTID 集合（Retrieved_Gtid_Set）。
                检查 GTID 是否已执行：
                从库会检查当前事务的 GTID 是否已经存在于自己的 gtid_executed 集合中。
                如果 GTID 已经存在，说明该事务已经执行过，从库会跳过该事务，避免重复执行。
                应用事务：
                如果 GTID 不存在于 gtid_executed 集合中，从库会将事务内容写入自己的 Relay Log（中继日志）。
                从库的 SQL 线程会读取 Relay Log 中的事务，并在本地执行。
                更新 GTID 集合：
                从事务成功执行后，从库会将该事务的 GTID 添加到自己的 gtid_executed 集合中。
                从库会定期将 gtid_executed 集合持久化到 mysql.gtid_executed 表中。

                4. GTID 复制的故障恢复
                如果从库在复制过程中遇到错误（如主键冲突或数据不一致），可以通过以下步骤恢复：

                停止从库复制：
                STOP SLAVE;

                跳过错误事务：

                手动设置 GTID，跳过错误事务：
                SET GTID_NEXT='source_id:transaction_id';
                BEGIN; COMMIT;
                SET GTID_NEXT='AUTOMATIC';
                将错误事务的 GTID 添加到 gtid_executed 集合中。

                重新启动从库复制：
                START SLAVE;



      GTID 主从：可以自动选主

      spring boot mysql 主从 ：sharding-jdbc-spring-boot-starter
     sharding-jdbc（shardingsphere）、mycat 读写分离，配置
     //sharding-jdbc 强制下一据查询主读
    HintManager.getInstance().setMasterRouteOnly();
    List<Order> b2 = orderMapper.findByUserId(6);


      mysql 主   备：
            主   从：建议一主多从，半同步复制
            多主多从：




      双主互为主从： mysql + keepalived  性能不如MMM，但MMM高并发有问题

      MHA：在主宕机，可以在从中选择（半同步复制）同步主日志的从作为主。
      mysql8 HA解决方案：
      (MMM 不维护) replication-manager:两主多从，只有一个主写，热备vip，每台服务器都要代理
      orchestrator:https://github.com/openark/orchestrator
      */





/*
方法 2：Orchestrator（官方推荐）
Orchestrator 是一个开源的 MySQL 高可用工具，支持 主从复制（Replication） 和 Group Replication，可以自动提升新的 Master。

工作流程
监控主库（Master）。
发现主库故障后，选择合适的 Slave 作为新 Master，并自动切换主从关系。
支持 自动 DNS 更新 或 VIP 变更，让应用程序透明地连接到新 Master。
优点

轻量级，MySQL 官方推荐。
Web 界面可视化管理数据库拓扑结构。
适用于大规模 MySQL 集群。
缺点
需要额外部署 Orchestrator 服务器。







应用程序连接配置更新
无论使用哪种方案，应用程序连接都需要更新，可以通过以下方式实现：

VIP（Virtual IP）：使用 Keepalived 绑定 VIP 到主库，Failover 时自动切换 VIP。
DNS 动态解析：更新 DNS 记录，指向新的 Master。
ProxySQL / HAProxy：应用程序连接 ProxySQL，由 ProxySQL 负责路由到正确的 Master。
环境变量：使用配置管理工具（如 Consul、Zookeeper）动态更新数据库连接。
 */


    //endregion

    //region   mysql ha 服务器架构
    /*
    3.2 基于 GTID 和半同步复制的高可用架构
        架构组成：
        1 台主库（Master）。
        2 台从库（Slave）。
        1 台管理节点（用于故障检测和切换）。
        工作流程：
        主库处理写操作，并通过 GTID 和半同步复制确保至少一个从库接收数据。
        管理节点监控主库和从库的状态。
        如果主库故障，管理节点选择一个从库提升为新的主库，并自动调整复制拓扑。
        优点：
        数据一致性更强。
        支持自动故障切换。
        缺点：
        需要更多服务器资源（至少 3 台）。
        配置和管理复杂度较高。


    3.4 基于 MHA（Master High Availability）的高可用架构
    架构组成：
            1 台主库（Master）。
            2 台从库（Slave）。
            1 台管理节点（MHA Manager）。
    工作流程：
    MHA Manager 监控主库和从库的状态。
    如果主库故障，MHA Manager 自动选择一个从库提升为新的主库，并调整其他从库的复制配置。
    优点：
    支持自动故障切换。
    配置相对简单。
    缺点：
    需要额外的管理节点。
    对网络和服务器性能有一定要求。
*/
    //endregion

    //region rbac
    //user
    //menu（树形结构）：区分menu_type 菜单和按钮权限
    //role
    //用户角色
    //角色菜单：包含按钮


    /*优化设计
    用户组（树形结构） ：用户加入用户组，指定用户组的角色。就不用新增用户时候赋值每个角色
    角色组 （树形结构）：角色组可避免新增功能时候为每个角色分配菜单权限
    */

    //不设计权限表：直接设计菜单权限，按钮权限和菜单区分，
    //endregion

    //region 发布
    /*蓝绿发布：两套环境并行， 可以快速回滚。   数据库采用一套新版本兼容老版本，其中一个版本只读（看情况），回滚时候
        任何添加到新版本的新数据也必须在回滚时传递给旧数据库。
        */

   //endregion

    //region rabbitmq
    /*
     rabbitmq 镜像模式  、仲裁队列（Quorum Queue）
     */
    //endregion

    //region 订单超时取消
    /*
    高并发、高精度要求	时间轮算法(netty HashedWheelTimer ) + 数据库批处理

    中小规模、低并发	Redis 过期事件 + 定时任务兜底
    高并发、高精度要求	时间轮算法 + 数据库批处理
    需要动态调整延迟时间	定时任务 + 外部配置（如 Apollo）
    已有 MQ 基础设施	MQ 延迟消息 + 定时任务补偿机制


    xxl-job  订单超时设计
    SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createTime < :expireTime

    查出超时未支付的订单，修改其状态为取消

     */
    //endregion


    //region H2 内存关系型数据库
    /*
    H2 是一个轻量级的开源 Java 数据库，通常用作嵌入式数据库或者作为开发阶段的临时数据库。它支持 SQL 和嵌入式模式，并且能够在内存中运行，具有非常快的性能。H2 的特点包括：
    嵌入式和服务器模式：可以作为独立的数据库引擎运行，也可以嵌入到 Java 应用程序中。
    内存模式：可以在内存中运行，无需磁盘存储，适用于快速原型和单元测试。
    SQL 兼容性：支持大多数常见的 SQL 标准，虽然在一些高级功能上不如 MySQL 或 PostgreSQL 那样全面。
    Web 控制台：内置一个 Web 控制台，方便进行数据库的管理和查询。
    支持多种存储方式：支持磁盘存储、内存存储以及混合存储。

    H2：适合需要较高性能、支持更多 SQL 特性并且可能需要独立服务器模式的场景。
    SQLite：适合嵌入式和轻量级应用，特别是在资源有限的环境中（如移动设备、桌面软件等）
    */

    //endregion

    //扣减库存 保证原子性
    //UPDATE goods_stock SET stock = stock - 1 WHERE stock > 0 AND id = 1001;

    //保证原子性
    //扣库存成功并写入订单信息到redis(lua)-->发送mq-->写入成功mq,redis 订单设置过期时间-->支付成功更新deb，否则redis库存incr 1



    //region 耗时请求
    /*
     耗时请求：前段请求的直接执行，直接只执行直到执行完，其他的rpc  直接进入rabbitmq.返回一个状态
             前段请求也可以进入mq，加一个数据状态 后台执行中，避免重复请求。sse 返回执行结果刷新前段。
     */

    //endregion



    //region  ClickHouse 、MySQL单线程处理写入  MPP架构

        /*
        MySQL 默认使用单线程处理写入操作（尤其是 InnoDB 引擎）
        默认情况下，MySQL 的写入操作（INSERT/UPDATE/DELETE）由 单线程执行，且需要维护事务日志（redo log）、二进制日志（binlog）等，导致 I/O 和锁竞争成为瓶颈。
        虽然支持多线程读取（通过并发连接），但写入仍依赖 行级锁 和 WAL（Write-Ahead Logging）机制，高并发写入时容易阻塞。

         批次大小：官方建议 每次写入不少于 1000 行，最好 10万行以上，以减少小文件生成和后台合并（Merge）的压力


         绝对不丢失：fsync_after_insert=1
         推荐选择：
            ClickHouse 24.8+：直接使用 Kafka 引擎 + exactly-once。
            大规模实时数仓：Flink + ClickHouse（如广告点击分析、日志监控）


             从kafka写入 clickhouse ：
            使用ClickHouse的Kafka Engine
            ClickHouse内置了Kafka引擎，可以直接消费Kafka数据并写入本地表。

            MySQL → Flink CDC → Kafka → Flink → ClickHouse
           Flink CDC捕获MySQL变更  -->写入Kafka中转-->Flink消费Kafka并写入ClickHouse


         */

    //endregion

    //region  RBAC和多租户

        /*
    RBAC和多租户是互补而非替代的关系：
    多租户是"横向"隔离，确保不同客户数据的物理/逻辑分离
    RBAC是"纵向"控制，管理客户内部人员的权限分配
    在成熟的SaaS系统设计中，应该：
    先实施多租户隔离：建立基础的数据安全边界
    再叠加RBAC：实现组织内部的精细化权限管理
    最后补充ABAC（如需要）：处理更复杂的属性基控制
          */

    //endregion

}

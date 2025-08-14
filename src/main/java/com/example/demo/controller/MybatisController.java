package com.example.demo.controller;

import com.example.demo.model.pojo.PageData;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.pojo.Page;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.service.demo.DemoProductService;
import com.example.demo.service.mybatissql.MybatisSqlService;
import com.example.demo.service.wms.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/*
https://mybatis.org/mybatis-3/zh/sqlmap-xml.html
 */
@RestController
@RequestMapping("/mybatis")
public class MybatisController {
    /*
    注解方式:不适合动态sql,写的比较繁杂，代码可读性不太好。功能如表单查询。
    原生SQL:还是要在xml定义方法名（mapper里方法），在service里调用mapper传入sql语句，还是没有摆脱xml。
    XML方式：
     */
    /*
     f返回整形：不必指定resultType,指定了还报错
     */

    /*
    ${}变量占位符：的功能是直接进行字符串拼接,将参数替换${param}。一般用于数据库基础连接配置。
    #{}参数占位符，变量会被变异成？。
     */


    /*
         一级缓存：要开启事务，在同一事务内两次查询利用缓存。实际没多大用
          二级缓存 <select id="getPageData" useCache="true" resultType="com.example.demo.model.viewModel.ProductVM" >
          resultType 要实现Serializable接口，可序列化。

         二级缓存：
         demo  wms.ProductMapper
         Cache Hit
        mapper设置  <cache eviction="LRU" flushInterval="100000" readOnly="true" size="1024"/>
        查询语句设置  <select id="getPageData" useCache="true" resultType="com.example.demo.model.viewModel.ProductVM" >

     */

    //region 一级缓存、二级缓存
    /**
     *一级缓存更新机制
     * 当执行更新操作（INSERT、UPDATE、DELETE）时，MyBatis 会自动清空一级缓存，这是为了确保数据的一致性。具体表现为：
     * 更新操作会清空所属的命名空间缓存：
     * 执行任何更新操作后，MyBatis 会清空该 SqlSession 中对应 Mapper 命名空间的一级缓存
     * 这是为了防止后续查询获取到脏数据
     * 不同 SqlSession 的缓存独立：
     * 每个 SqlSession 有自己的一级缓存
     * 一个 SqlSession 的更新不会直接影响其他 SqlSession 的缓存
     *
     *局部禁用缓存：在 select 语句上设置 flushCache="true"
     * <select id="selectById" resultType="User" flushCache="true">
     *   SELECT * FROM user WHERE id = #{id}
     * </select>
     *
     * 作用域范围：
     * 每个 SqlSession 拥有自己独立的一级缓存
     * 缓存的生命周期与 SqlSession 相同
     * 不同 SqlSession 之间的缓存完全隔离
     *
     * 缓存共享性：
     * 同一个 SqlSession 内的多次操作共享缓存
     * 不同 SqlSession 即使查询相同数据也不会共享缓存
     *
     *
     * 可以通过 MyBatis 配置调整一级缓存行为：
     *
     * xml
     * <settings>
     *   <!-- 可选值: SESSION(默认) | STATEMENT -->
     *   <setting name="localCacheScope" value="SESSION"/>
     * </settings>
     * SESSION：会话级别缓存（默认）
     * STATEMENT：语句级别缓存（相当于禁用一级缓存）
     *
     *
     * 缓存失效场景：
     * 执行任何 INSERT/UPDATE/DELETE 操作
     * 调用 sqlSession.clearCache() 方法
     * 提交事务 (commit())
     * 回滚事务 (rollback())
     * 关闭 SqlSession
     *
     * 事务影响：
     * 未提交的修改对其他 SqlSession 不可见
     * 事务提交后，其他 SqlSession 需要重新查询获取最新数据
     *
     *
     *一级缓存 (Local Cache)
     * 基本特性
     * 作用范围：SqlSession 级别（会话级别）
     * 生命周期：随 SqlSession 的创建而创建，随 SqlSession 的关闭而销毁
     * 默认状态：默认开启，不可关闭（但可配置为 STATEMENT 级别）
     * 共享性：同一个 SqlSession 内共享
     *
     * 工作特点
     * 同一个 SqlSession 中执行相同的 SQL 查询，第二次会直接从缓存获取
     * 执行任何 INSERT/UPDATE/DELETE 操作都会清空当前 SqlSession 的一级缓存
     * 调用 clearCache() 方法可手动清空
     * 不同 SqlSession 之间缓存不共享
     * 配置
     *<settings>
     *   <!-- 可选值：SESSION(默认)/STATEMENT -->
     *   <setting name="localCacheScope" value="SESSION"/>
     * </settings>
     *
     *
     * 二级缓存 (Second Level Cache)
     * 基本特性
     * 作用范围：Mapper 级别（namespace 级别）
     *
     * 生命周期：与应用生命周期相同
     * 默认状态：默认关闭，需要显式开启
     * 共享性：多个 SqlSession 共享
     *
     * 工作特点
     * 跨 SqlSession 有效，不同会话可以共享缓存数据
     * 缓存以 namespace 为单位，不同 namespace 操作互不影响
     * 执行 INSERT/UPDATE/DELETE 操作会清空对应 namespace 的二级缓存
     * 支持第三方缓存实现（Ehcache、Redis等）
     *
     * 配置方式
     * 全局开启：
     * <settings>
     *   <setting name="cacheEnabled" value="true"/>
     * </settings>
     * 在 Mapper.xml 中启用：
     * <mapper namespace="com.example.mapper.UserMapper">
     *   <cache/> <!-- 简单配置 -->
     *   <!-- 详细配置示例 -->
     *   <cache
     *     eviction="FIFO"
     *     flushInterval="60000"
     *     size="512"
     *     readOnly="true"/>
     * </mapper>
     */
//endregion

    @Autowired
    private MybatisSqlService mybatisSqlService;

    @Autowired
    private DemoProductService demoProductService;

    @RequestMapping("/getPageData")
    public MessageResult<PageData<ProductVM>> getPageData(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            message = mybatisSqlService.getPageData(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping("/getPageDataByHelper")
    public MessageResult<PageData<ProductVM>> getPageDataByHelper(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            message = mybatisSqlService.getPageDataByHelper(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping("/getPageDataByHelperCTE")
    public MessageResult<PageData<ProductVM>> getPageDataByHelperCTE(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            message = mybatisSqlService.getPageDataByHelperCTE(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    /*
   在不设置时，也就是默认情况是任何方式的请求都可以(不管什么get/post/.....)
在文件上传的时候只能用post（因为其post请求（数据量可以很大）是在请求体里面，而get请求（且数据量有限）是在路径后面进行拼接）
其中RequestMapping在设置Method属性后意义为不仅要满足其value属性还要满足method属性了（RequestMethod.GET）

     */
    @RequestMapping(value = "/concatSelect")
    public MessageResult<PageData<ProductVM>> concatSelect(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            message = mybatisSqlService.concatSelect(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/test")
    public MessageResult<PageData<Void>> test() {
        MessageResult<PageData<Void>> message = new MessageResult<>();

//        demoProductService.insertTransactional();
        demoProductService.selectByPrimaryKey(999);

        return message;
    }


}

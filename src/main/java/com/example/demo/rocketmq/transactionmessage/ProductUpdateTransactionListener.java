package com.example.demo.rocketmq.transactionmessage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.dao.demo.ProductTestMapper;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.service.demo.IMqMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.elasticsearch.client.license.LicensesStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Optional;

/**
 * 事务监听器实现
 *
 * 设计重点
 * 本地事务 = 业务表 + 事务日志表
 * 必须在 同一个数据库事务
 *
 * 本地事务：executeLocalTransaction 要执行业务数据+事务日志 用于回查
 * 事务日志表:回查确认依据（checkLocalTransaction ）
 */
@Slf4j
@RocketMQTransactionListener()
public class ProductUpdateTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired
    private ProductTestMapper productTestMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private IMqMessageService mqMessageService;


    /**
     * 执行本地事务
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {

            //生产环境配置：多事务消息 + 多 Producer Group + 多 Template + 多 Listener
            String txType = (String) message.getHeaders().get("tx_type");
            String transactionId = (String) message.getHeaders().get("TRANSACTION_ID");
            switch (txType) {
                case "PRODUCT_TEST_UPDATE":
                    // ======= 本地事务逻辑 =======
                    //操作数据库事务

//                    1、执行业务
//                    message.getPayload(); 是byte[]

                    ProductTest productTest = (ProductTest) o;
                    productTest.setVersion(productTest.getVersion() + 1);
                    LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
                    updateWrapper3.set(ProductTest::getVersion, productTest.getVersion());
                    updateWrapper3.eq(ProductTest::getId, productTest.getId());
                    productTestMapper.update(updateWrapper3);
                    int n = Integer.parseInt("n");


                    break;
                case "ORDER_CREATE":
                    break;

                default:
                    return RocketMQLocalTransactionState.ROLLBACK;

            }
//                  2、插入事务日志表，或者本地消息表
//            transactionId
            MqMessage mqMessage = new MqMessage
                    ("",
                            "",
                            txType,
                            transactionId);
            mqMessage.setSendMq(false);
            //BusinessId 设计的是long，暂时放在msgContent。后期改成String
//            mqMessage.setBusinessId((long) productTest.getId().intValue());

            mqMessageService.save(mqMessage);

            Thread.sleep(8000);
//            return RocketMQLocalTransactionState.UNKNOWN; 或者事务执行耗时超过6s进入checkLocalTransaction 回查
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            log.error("本地事务异常", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }

    }

    /**
     *
     * 回查：
     * 会触发回查的情况
     * Producer 返回 UNKNOWN
     * return RocketMQLocalTransactionState.UNKNOWN;
     *
     *
     * Producer 崩溃 / 网络异常
     * executeLocalTransaction 执行中 JVM crash
     * Producer 执行完本地事务但没来得及返回 COMMIT/ROLLBACK
     * Broker 在事务超时内没有收到最终状态
     * 默认 transactionTimeout = 6s
     * transactionCheckMax = 15
     *
     * @param message
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        //事务校验成功：校验数据库事务是否操作成功
        boolean checkSuccess = false;
        try {


            String productTestIdStr = Optional.ofNullable(message.getHeaders().get("business_id").toString()).orElse("");
            if (StringUtils.isEmpty(productTestIdStr)) {

                return RocketMQLocalTransactionState.ROLLBACK;
            }
            Long productTestId = Long.parseLong(productTestIdStr);
            ProductTest db = productTestMapper.selectById(productTestId);

            String transactionId = (String) message.getHeaders().get("TRANSACTION_ID");
            //BusinessId 设计的是long，暂时放在msgContent。后期改成String
            LambdaQueryWrapper<MqMessage> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MqMessage::getMsgContent, transactionId);
            List<MqMessage> mqMessageList = this.mqMessageService.list(queryWrapper);
            int size = mqMessageList.size();
            if (size == 0) {
                log.info("Can not find transactionId {} fail", transactionId);
            } else if (size > 1) {
                log.info("Multiple transactionId {} fail", transactionId);
            } else {
                log.info("Check transactionId {} success", transactionId);
                checkSuccess = true;
            }
        } catch (Exception ex) {
            log.error("", ex);
        }
        //在本地
        //回查成功
        return checkSuccess ? RocketMQLocalTransactionState.COMMIT :
                RocketMQLocalTransactionState.ROLLBACK;
    }
}

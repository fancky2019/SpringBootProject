package com.example.demo.service.wms;

import com.example.demo.dao.wms.OrderDetailMapper;
import com.example.demo.dao.wms.OrderMapper;
import com.example.demo.model.entity.wms.Order;
import com.example.demo.model.entity.wms.OrderDetail;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.OrderManagerVM;
import com.example.demo.service.demo.PersonService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Timestamp;
import java.util.UUID;

/*
编程式事务:TransactionManager 手动管理事务
声明式事务:@Transactional


@Transactional 注解一般可以作用在类或者方法上。 作用于类：当把@Transactional 注解放在类上时，表示所有该类的 public 方法都配置相同的事务属性信息。
 作用于方法：当类配置了@Transactional，方法也配置了@Transactional，方法的事务会覆盖类的事务配置信息。

springboot 支持单数据源事务。至于多数据源事务参照分布式事务。
springboot开启事务很简单，只需要一个注解@Transactional 就可以了。
因为在springboot中已经默认对jpa、jdbc、mybatis开启了事事务，
引入它们依赖的时候，事物就默认开启
 */
@Service
// @Transactional(rollbackFor = Exception.class)
public class OrderManagerService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;

    /*
    REQUIRED ：如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。 default Propagation.REQUIRED
    SUPPORTS ：如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务的方式继续运行。
    MANDATORY ：如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。
    REQUIRES_NEW ：创建一个新的事务，如果当前存在事务，则把当前事务挂起。
    NOT_SUPPORTED ：以非事务方式运行，如果当前存在事务，则把当前事务挂起。
    NEVER ：以非事务方式运行，如果当前存在事务，则抛出异常。
    NESTED ：如果当前存在事务，则创建一个事务作为当前事务的嵌套事务来运行；如果当前没有事务，则该取值等价于 REQUIRED 。
    指定方法：通过使用 propagation 属性设置，例如：@Transactional(propagation = Propagation.REQUIRED)
     */

    /*
    Propagation propagation() default Propagation.REQUIRED;
    Isolation isolation() default Isolation.DEFAULT;
     */

    /*
    自动回滚：  在@Transactional注解中如果不配置rollbackFor属性,那么事物只会在遇到RuntimeException的时候才会回滚,
    加上rollbackFor=Exception.class,Exception还要抛出。 可以让事物在遇到非运行时异常时也回滚

    手动回滚：TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
     */
   // @Transactional(isolation = Isolation.DEFAULT,propagation = Propagation.REQUIRED)//默认
    @Transactional
//    @Transactional(rollbackFor = Exception.class)

    //事务隔离级别：默认数据库的mysql repeatable read ;msql:read commit，事务传播: Propagation.REQUIRED;
//    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public MessageResult<Void> addOrder(OrderManagerVM orderManagerVM) {
        MessageResult<Void> messageResult = new MessageResult<>();

        //获取当前类的代理对象
//        OrderManagerService orderManagerService = (OrderManagerService) AopContext.currentProxy();
        try {
            Order order = new Order();
            order.setGuid(UUID.randomUUID().toString());
            order.setOrdernumber(orderManagerVM.getOrderNumber());
            order.setCreatetime(new Timestamp(System.currentTimeMillis()));
            order.setOrdertype(1);
            order.setStatus(Short.parseShort("1"));
            Integer result = orderMapper.insert(order);
            if (result <= 0) {
                messageResult.setMessage("保存失败");
                messageResult.setSuccess(false);
                return messageResult;
            }
            //获取刚插入的订单，生成的订单ID
            Integer orderId = order.getId();
//            orderManagerVM.getProductOrderDetail().forEach(p ->
            for (OrderDetail p : orderManagerVM.getProductOrderDetail()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderid(orderId);
                orderDetail.setGuid(UUID.randomUUID().toString());
                orderDetail.setProductid(p.getProductid());
                orderDetail.setCount(p.getCount());
                orderDetail.setDealprice(p.getDealprice());
                orderDetail.setStatus(Short.parseShort("1"));
                Integer re = orderDetailMapper.insert(orderDetail);
                if (re <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    messageResult.setMessage("保存失败");
                    messageResult.setSuccess(false);
                    return messageResult;
                }
            }
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            //事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        } finally {
            return messageResult;
        }
    }

    /*
    {
	"orderNumber":"12345678",
	"productOrderDetail":[
		{
			"productid":2115,
			"count":30,
			"dealprice":25.5
		},
			{
			"productid":2114,
			"count":208,
			"dealprice":20
		}
		]

}
     */
    @Transactional(rollbackFor = Exception.class)
    public MessageResult<Void> addOrderAndOrderDetails(OrderManagerVM orderManagerVM) throws Exception {
        MessageResult<Void> messageResult = new MessageResult<>();

        Order order = new Order();
        order.setGuid(UUID.randomUUID().toString());
        order.setOrdernumber(orderManagerVM.getOrderNumber());
        order.setCreatetime(new Timestamp(System.currentTimeMillis()));
        order.setOrdertype(1);
        order.setStatus(Short.parseShort("1"));
        Integer result = orderMapper.insert(order);
        if (result <= 0) {
            throw new Exception("保存失败");
//            messageResult.setMessage("保存失败");
//            messageResult.setSuccess(false);
//
//            return messageResult;
        }
        //获取刚插入的订单，生成的订单ID
        Integer orderId = order.getId();
//            orderManagerVM.getProductOrderDetail().forEach(p ->
        for (OrderDetail p : orderManagerVM.getProductOrderDetail()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderid(orderId);
            orderDetail.setGuid(UUID.randomUUID().toString());
            orderDetail.setProductid(p.getProductid());
            orderDetail.setCount(p.getCount());
            orderDetail.setDealprice(p.getDealprice());
            orderDetail.setStatus(Short.parseShort("1"));


            //  Integer m=Integer.valueOf("m");

            Integer re = orderDetailMapper.insert(orderDetail);
            if (re <= 0) {
                throw new Exception("保存失败");
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                messageResult.setMessage("保存失败");
//                messageResult.setSuccess(false);
//                return messageResult;
            }
        }
        messageResult.setSuccess(true);
        return messageResult;
    }

    /*
     自动回滚在try...catch中没有继续抛出异常不能自动回滚
     */
    @Transactional(rollbackFor = Exception.class)
    public MessageResult<Void> addOrderAndOrderDetailsTry(OrderManagerVM orderManagerVM) throws Exception {


        MessageResult<Void> messageResult = new MessageResult<>();
        try {

            Order order = new Order();
            order.setGuid(UUID.randomUUID().toString());
            order.setOrdernumber(orderManagerVM.getOrderNumber());
            order.setCreatetime(new Timestamp(System.currentTimeMillis()));
            order.setOrdertype(1);
            order.setStatus(Short.parseShort("1"));
            Integer result = orderMapper.insert(order);
            if (result <= 0) {

                messageResult.setMessage("保存失败");
                messageResult.setSuccess(false);

            } else {
                messageResult.setSuccess(true);
            }
            //获取刚插入的订单，生成的订单ID
            Integer orderId = order.getId();
//            orderManagerVM.getProductOrderDetail().forEach(p ->
            for (OrderDetail p : orderManagerVM.getProductOrderDetail()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderid(orderId);
                orderDetail.setGuid(UUID.randomUUID().toString());
                orderDetail.setProductid(p.getProductid());
                orderDetail.setCount(p.getCount());
                orderDetail.setDealprice(p.getDealprice());
                orderDetail.setStatus(Short.parseShort("1"));


                Integer m = Integer.valueOf("m");

                Integer re = orderDetailMapper.insert(orderDetail);
                if (re <= 0) {
                    messageResult.setMessage("保存失败");
                    messageResult.setSuccess(false);

                } else {
                    messageResult.setSuccess(true);
                }
            }
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);

            //如果不抛出异常，将不能自动回滚
            throw ex;
        }


        return messageResult;
    }

    /*
    回滚事务、手动回滚事务

    自动回滚
    @Transactional默认只回滚RunTimeException级别，
    如果需要回滚到Exception级别才需要指定@Transactional(rollbackFor=Exception.class) ，Exception还要抛出。
    表示Exception级别及一下均会回滚
     */
    @Transactional(rollbackFor = Exception.class)
    public MessageResult<Void> deleteOrder(Order order) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
//                OrderDetail orderDetail=new OrderDetail();
//                orderDetail.setOrderid(order.getId());
//                List<OrderDetail> orderDetailList=orderDetailMapper.select(orderDetail);
            Integer result = orderMapper.deleteByPrimaryKey(order.getId());
            if (result <= 0) {
                messageResult.setMessage("删除失败");
                messageResult.setSuccess(false);
                return messageResult;
            }
            Integer re = orderDetailMapper.deleteByOrderId(order);
            if (re <= 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                messageResult.setMessage("删除失败");
                messageResult.setSuccess(false);
                return messageResult;
            }
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            //事务回滚 手动回滚
            //TransactionAspectSupport
            //PlatformTransactionManager
            //TransactionTemplate提供了更简洁的API来管理事务。它隐藏了底层的PlatformTransactionManager的使用
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            //或者
//            throw  new  Exception(ex.getMessage());
//            throw ex;
        } finally {
            return messageResult;
        }
    }


}

package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.entity.wms.Product;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.service.rabc.UserManagerService;
import com.rabbitmq.client.Return;
import jdk.nashorn.internal.ir.ReturnNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
//import java.sql.*;

import java.util.*;
import java.util.Date;

/**
 * execute：可以执行所有SQL语句，一般用于执行DDL语句。
 * update：用于执行INSERT、UPDATE、DELETE等DML语句。不能执行存储过程
 * 内部调用JDBC的PreparedStatement的executeUpdate
 * queryXxx：用于DQL数据查询语句。
 */


@RestController
@RequestMapping("/JdbcTemplate")
public class JdbcTemplateController {
    private static Logger logger = LogManager.getLogger(UserManagerService.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getPageData")
    public MessageResult<PageData<ProductVM>> getPageData(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            String selectCommand = "  select * from [dbo].[Product] p\n" +
                    "        join [dbo].[Stock] s on p.StockID=s.ID\n" +
                    "        left join [dbo].[BarCode] b on p.BarCodeID=b.ID\n" +
                    "        join [dbo].[Sku] sk on p.SkuID=sk.ID\n" +
                    "        where 1=1\n" +
                    "ORDER BY 1\n" +
                    " OFFSET ?*(?-1) ROWS FETCH NEXT ? ROWS ONLY";
            Integer pageSize = 10;
            Integer pageIndex = 1;
            //设置sql语句中的占位符(?)参数，内部封装了PreparedStatement参数赋值过程
            Object[] args = new Object[]{pageSize, pageIndex, pageSize};
//

            String selectCountCommand = "  select count(*) from [dbo].[Product] p\n" +
                    "        join [dbo].[Stock] s on p.StockID=s.ID\n" +
                    "        left join [dbo].[BarCode] b on p.BarCodeID=b.ID\n" +
                    "        join [dbo].[Sku] sk on p.SkuID=sk.ID\n" +
                    "        where 1=1";
            //queryForList 内部调用的还是query
            List<ProductVM> list = jdbcTemplate.query(selectCommand, args, new BeanPropertyRowMapper<>(ProductVM.class));
            Integer count = jdbcTemplate.queryForObject(selectCountCommand, Integer.class);
            String countString = jdbcTemplate.queryForObject(selectCountCommand, String.class);
            String insertCommand = "  insert into Product ( GUID, StockID," +
                    "      BarCodeID, SkuID, ProductName," +
                    "      ProductStyle, Price, CreateTime, " +
                    "      Status, COUNT , ModifyTime " +
                    "      )" +
                    "      values (?,?,?,?,?,?,?,?,?,?,?)";

            Product product = getProduct();
            Object[] params = new Object[]{product.getGuid(), product.getSkuid(), product.getBarcodeid(),
                    product.getSkuid(), product.getProductname(), product.getProductstyle(),
                    product.getPrice(), product.getCreatetime(), product.getStatus(),
                    product.getCount(), product.getModifytime()
            };
            Integer reflectionCount = jdbcTemplate.update(insertCommand, params);
            reflectionCount = jdbcTemplate.update(insertCommand, params);
            KeyHolder keyHolder = new GeneratedKeyHolder();

            //获取插入生成的ID
            reflectionCount = jdbcTemplate.update((connnection) ->
            {
                //和JDBC的一样
                PreparedStatement preparedStatement = connnection.prepareStatement(insertCommand, Statement.RETURN_GENERATED_KEYS);
                //设置参数
                preparedStatement.setString(1, UUID.randomUUID().toString());
                preparedStatement.setInt(2, 1);
                preparedStatement.setNull(3, Types.INTEGER);
                preparedStatement.setInt(4, 1);
                preparedStatement.setString(5, "fanckyProductname");
//                preparedStatement.setNull(6, Types.NVARCHAR);

                preparedStatement.setString(6, "productStyle");
                preparedStatement.setBigDecimal(7, new BigDecimal(123));
                //                 setDate只能得到年月日
                preparedStatement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                Short status = 1;
                preparedStatement.setShort(9, status);
                preparedStatement.setInt(10, 12);
                preparedStatement.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
                return preparedStatement;
            }, keyHolder);
            Number id = keyHolder.getKey();//preparedStatement.getGeneratedKeys();
            String deleteCommand = "delete from [WMS].[dbo].[Product] where ID=?";
            reflectionCount = jdbcTemplate.update(deleteCommand, new Object[]{6});
            String updateCommand = "  update [WMS].[dbo].[Product] set ProductName='prod4' where id=?";
            reflectionCount = jdbcTemplate.update(updateCommand, new Object[]{7});


//         //   jdbcTemplate.batchUpdate(selectCommand);

//          //重载原因，使用lamda表达式无法识别重载的方法,lambda强制类型转换成对应函数接口
//            List<Product> re = jdbcTemplate.execute(connection -> {
//                String execProc = "{call GetProductProc(?,?)}";
//                CallableStatement callableStatement = connection.prepareCall(execProc);
//                callableStatement.setInt(1, 12);
//                callableStatement.setString(2, "pr");
//                return callableStatement;
//              },
//                    (CallableStatementCallback<List<Product>>) callableStatement -> {
//                        callableStatement.execute();
//                        ResultSet resultSet = callableStatement.getResultSet();
//                        List<Product> list1 = new ArrayList<>();
//                        try {
//                            list1 = convertToList(resultSet, Product.class);
//                        } catch (Exception ex) {
//                            System.out.println(ex.getMessage());
//                        }
//                        return list1;
//
//                    });

            List<Product> re = jdbcTemplate.execute(new CallableStatementCreator() {
                                                        @Override
                                                        public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                                                            String execProc = "{call GetProductProc(?,?)}";
                                                            CallableStatement callableStatement = connection.prepareCall(execProc);
                                                            callableStatement.setInt(1, 12);
                                                            callableStatement.setString(2, "pr");
                                                            return callableStatement;
                                                        }
                                                    },
                    new CallableStatementCallback<List<Product>>() {
                        @Override
                        public List<Product> doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
                            callableStatement.execute();
                            ResultSet resultSet = callableStatement.getResultSet();
                            List<Product> list = new ArrayList<>();
                            try {
                                list = convertToList(resultSet, Product.class);
//                                list=resultSetToList(resultSet,Product.class);
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                            return list;

                        }
                    });
            int m = 0;

        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }


    public static <T> List<T> resultSetToList(ResultSet resultSet, Class<T> clazz) {

        //创建一个 T 类型的数组
        List<T> list = new ArrayList<>();
        try {
            //通过反射获取对象的实例
            T t = clazz.getConstructor().newInstance();
            //获取resultSet 的列的信息
            ResultSetMetaData metaData = resultSet.getMetaData();
            //遍历resultSet
            while (resultSet.next()) {
                //遍历每一列
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    //获取列的名字
                    String fName = metaData.getColumnLabel(i + 1);
                    try {

//if(fName.equals("TimeStamp"))
//{
//    int m=0;
//}
                        //因为列的名字和我们EMP中的属性名是一样的，所以通过列的名字获得其EMP中属性
                        Field field = clazz.getDeclaredField(fName.toLowerCase());
                        //因为属性是私有的，所有获得其对应的set 方法。set+属性名首字母大写+其他小写
                        String setName = "set" + fName.toUpperCase().substring(0, 1) + fName.substring(1).toLowerCase();
                        //因为属性的类型和set方法的参数类型一致，所以可以获得set方法
                        Method setMethod = clazz.getMethod(setName, field.getType());
                        //执行set方法，把resultSet中的值传入emp中，  再继续循环传值
                        setMethod.invoke(t, resultSet.getObject(fName));
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        int m = 0;
                    }
                }
                //把赋值后的对象 加入到list集合中
                list.add(t);
            }

        } catch (Exception e) {
            String str = e.getMessage();
            e.printStackTrace();
        }
        // 返回list
        return list;
    }

    public static <T> List<T> convertToList(ResultSet rs, Class<T> t) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();
        int columnCount = md.getColumnCount();
        while (rs.next()) {
            Map<String, Object> rowData = new HashMap<String, Object>();
            for (int i = 1; i <= columnCount; i++) {
                try {


                    Object obj = rs.getObject(i);
                    rowData.put(md.getColumnName(i), obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    int m = 0;
                }
            }
            list.add(rowData);
        }
        String jsonString = JSON.toJSONString(list);
        List<T> resultList = JSON.parseArray(jsonString, t);
        return resultList;
    }

    private Product getProduct() {
        Product product = new Product();
        product.setGuid(UUID.randomUUID().toString());
        product.setStockid(1);
        product.setBarcodeid(1);
        product.setSkuid(1);
        product.setProductname("productname4");
        product.setProductstyle("Productstyle4");
        product.setPrice(BigDecimal.valueOf(12));
        product.setCreatetime(new Date());
        product.setStatus(Short.valueOf("1"));
        product.setCount(1);
        product.setModifytime(new Date());
        return product;
    }

    // SpringMVC的自动装箱（实体类接收参数）
    //post提交 data:{}是一个对象，要用对象接收，类的访问级别是共有，否则MVC反射找不到报。
    //  @ResponseBody  返回业务对象，不要返回字符串，不然前台无法转换JSON而报错，还要Json 序列化操作。
    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addUser")
    public void addProduct(@RequestBody Users user) {
        try {
            int a = user.getId();
        } catch (Exception ex) {
            String s = ex.getMessage();
        }
    }

}

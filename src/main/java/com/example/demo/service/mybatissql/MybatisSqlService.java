package com.example.demo.service.mybatissql;

import com.example.demo.dao.mybatissql.MybatisSqlMapper;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.pojo.Page;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.service.rabc.UserManagerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MybatisSqlService {

    private static Logger logger = LogManager.getLogger(UserManagerService.class);

    @Autowired
    MybatisSqlMapper mybatisSqlMapper;



    public MessageResult<PageData<ProductVM>> getPageData(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
//            String sql=String.format("",);
            String sql=String.format(" select * from WMS.[dbo].[Product] p\n" +
                    "          join  WMS.[dbo].[Stock] s on p.StockID=s.ID\n" +
                    "          left join  WMS.[dbo].[BarCode] b on p.BarCodeID=b.ID\n" +
                    "          join  WMS.[dbo].[Sku] sk on p.SkuID=sk.ID\n" +
                    "          where 1=1");
            List<ProductVM> list = mybatisSqlMapper.getPageData(sql);
            PageData<ProductVM> pageData = new PageData<>();
            pageData.setData(list);
            message.setData(pageData);
            message.setSuccess(true);
        } catch (Exception ex) {
            message.setSuccess(false);
            message.setMessage(ex.getMessage());
            logger.error("",ex);
        } finally {
            return message;
        }
    }
    public MessageResult<PageData<ProductVM>> getPageDataByHelper(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {

            PageData<ProductVM> pageData = new PageData<>();
            String orderBy = "p.[CreateTime] desc";//按照（数据库）排序字段 倒序 排序
//
////            PageHelper.startPage(pageNum, pageSize, orderBy);
//
//
//            // 开启分页插件,放在查询语句上面 帮助生成分页语句
//            PageHelper.startPage(viewModel.getPageIndex(), viewModel.getPageSize(),orderBy);
//            List<ProductVM> list = mybatisSqlMapper.getPageDataByHelper(viewModel);
//           // 封装分页之后的数据  返回给客户端展示  PageInfo做了一些封装 作为一个类
//            PageInfo<ProductVM> pageInfo = new PageInfo<>(list);
//
//            pageData.setCount(pageInfo.getPages());
//            pageData.setData(pageInfo.getList());
//
//
//
//            message.setData(pageData);
            message.setSuccess(true);
        } catch (Exception ex) {
            message.setSuccess(false);
            message.setMessage(ex.getMessage());
            logger.error("",ex);
        } finally {
            return message;
        }
    }

    /**
     * 只支持select 开始的语句，不支持sql语句不是select cte
     * @param viewModel
     * @return
     */
    public MessageResult<PageData<ProductVM>> getPageDataByHelperCTE(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {

            PageData<ProductVM> pageData = new PageData<>();
//            String orderBy = "p.[CreateTime] desc";//按照（数据库）排序字段 倒序 排序
//
////            PageHelper.startPage(pageNum, pageSize, orderBy);
//
//
//            // 开启分页插件,放在查询语句上面 帮助生成分页语句
//            PageHelper.startPage(viewModel.getPageIndex(), viewModel.getPageSize(),orderBy);
//            List<ProductVM> list = mybatisSqlMapper.getPageDataByHelperCTE(viewModel);
//            // 封装分页之后的数据  返回给客户端展示  PageInfo做了一些封装 作为一个类
//            PageInfo<ProductVM> pageInfo = new PageInfo<>(list);
//
//            pageData.setCount(pageInfo.getPages());
//            pageData.setData(pageInfo.getList());
//
//
//
//            message.setData(pageData);
            message.setSuccess(true);
        } catch (Exception ex) {
            message.setSuccess(false);
            message.setMessage(ex.getMessage());
            logger.error("",ex);
        } finally {
            return message;
        }
    }

    public MessageResult<PageData<ProductVM>> concatSelect(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {

            List<ProductVM> list = mybatisSqlMapper.concatSelect(viewModel);
            PageData<ProductVM> pageData = new PageData<>();
            pageData.setData(list);
            message.setData(pageData);
            message.setSuccess(true);
        } catch (Exception ex) {
            message.setSuccess(false);
            message.setMessage(ex.getMessage());
            logger.error("",ex);
        } finally {
            return message;
        }
    }



}

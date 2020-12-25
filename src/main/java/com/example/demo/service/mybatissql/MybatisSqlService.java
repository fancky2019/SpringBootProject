package com.example.demo.service.mybatissql;

import com.example.demo.dao.mybatissql.MybatisSqlMapper;
import com.example.demo.dao.wms.ProductMapper;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
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
            PageData<ProductVM> paegData = new PageData<>();
            paegData.setData(list);
            message.setData(paegData);
            message.setSuccess(true);
        } catch (Exception ex) {
            message.setSuccess(false);
            message.setMessage(ex.getMessage());
            logger.error(ex.toString());
        } finally {
            return message;
        }
    }
}

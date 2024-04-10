//package com.example.demo.easyexcel.handler;
//
//import cn.hutool.json.JSONUtil;
//import com.alibaba.excel.context.AnalysisContext;
//import com.alibaba.excel.event.AnalysisEventListener;
//import com.fnd.excel.entity.Rule;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ExcelListener extends AnalysisEventListener<Rule> {
//
//    private static final Logger logger = LoggerFactory.getLogger(ExcelListener.class);
//
//    public List<Rule> getDataList() {
//        return dataList;
//    }
//
//    private List<Rule> dataList = new ArrayList<>();
//
//    @Override
//    public void invoke(Rule data, AnalysisContext context) {
//        logger.info("解析到一条数据:{}", JSONUtil.toJsonStr(data));
//        dataList.add(data);
//    }
//
//    @Override
//    public void doAfterAllAnalysed(AnalysisContext context) {
//        logger.info("数据解析完成");
//    }
//
//    /**
//     * 入库
//     */
//    private void saveData() {
//        logger.info("{}条数据，开始存储数据库！", dataList.size());
//        //这个方法自己实现  能完成保存数据入库即可
//        logger.info("存储数据库成功！");
//    }
//}

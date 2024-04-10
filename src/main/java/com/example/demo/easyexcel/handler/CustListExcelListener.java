//package com.example.demo.easyexcel.handler;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.excel.context.AnalysisContext;
//import com.alibaba.excel.event.AnalysisEventListener;
//import com.alibaba.excel.exception.ExcelAnalysisException;
//import com.alibaba.excel.exception.ExcelAnalysisStopException;
//import com.cpic.framework.admin.sdk.helper.ApplicationContextHelper;
//import com.fnd.businessvehicleintelligent.tongbao.constants.TongBaoConstant;
//import com.fnd.businessvehicleintelligent.tongbao.entity.TBCustListInfoVO;
//import com.fnd.core.exception.BizException;
//import com.fnd.core.utils.RedisUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//
//@Slf4j
//public class CustListExcelListener extends AnalysisEventListener<TBCustListInfoVO> {
//
//    private TBCustListInfoVO custListInfo;
//
//    List<TBCustListInfoVO> excelCustList = new ArrayList<>();
//
//    List<TBCustListInfoVO> custListCompare = new ArrayList<>();
//
//    List<TBCustListInfoVO> errorList = new ArrayList<>();
//
//    List<TBCustListInfoVO> checkedDataList = new ArrayList<>();
//
//    public List<TBCustListInfoVO> getErrorList() {
//        return errorList;
//    }
//
//    public List<TBCustListInfoVO> getCheckedDataList() {
//        return checkedDataList;
//    }
//
//    public TBCustListInfoVO getCustListInfo() {
//        return custListInfo;
//    }
//
//    public void setCustListInfo(TBCustListInfoVO custListInfo) {
//        this.custListInfo = custListInfo;
//    }
//
//    public List<TBCustListInfoVO> getExcelCustList() {
//        return excelCustList;
//    }
//
//    private RedisUtil redisUtil = (RedisUtil)ApplicationContextHelper.getBean(RedisUtil.class);
//
//    private Map<Object,Object> certTypeMap = redisUtil.hmget(TongBaoConstant.TBRedisPrefix.TB_CODE_VALUE.getPrefixCode() + "certType");
//
//    @Override
//    public void invoke(TBCustListInfoVO data, AnalysisContext context) {
//        //对每一条数据做校验
//        TBCustListInfoVO compareBean = new TBCustListInfoVO();
//        BeanUtil.copyProperties(data,compareBean);
//        if (TongBaoConstant.CustTypeEnum.POLICY_HOLDER.getCustTypeName().equals(data.getCustType())){
//            data.setCustType(TongBaoConstant.CustTypeEnum.POLICY_HOLDER.getCustTypeCode());
//        }else if (TongBaoConstant.CustTypeEnum.INSURANT.getCustTypeName().equals(data.getCustType())){
//            data.setCustType(TongBaoConstant.CustTypeEnum.INSURANT.getCustTypeCode());
//        }else if(TongBaoConstant.CustTypeEnum.CAR_OWNER.getCustTypeName().equals(data.getCustType())){
//            data.setCustType(TongBaoConstant.CustTypeEnum.CAR_OWNER.getCustTypeCode());
//        }else {
//            data.setIsVaild("0");
//            data.setErrorInfo("未知的客户类型");
//            errorList.add(data);
//            log.info("未知的客户类型，请检查excel文件");
//            throw new ExcelAnalysisException("未知的客户类型，请检查excel文件");
//        }
//        //数据状态默认为校验通过
//        data.setIsVaild("1");
//        //身份证类型转换
//        String certType = (String)certTypeMap.get(data.getCertType());
//        if (StringUtils.isBlank(certType)){
//            data.setIsVaild("0");
//            data.setErrorInfo("未知的证件类型");
//            errorList.add(data);
//            log.info("未知的证件类型，请检查excel文件");
//            throw new ExcelAnalysisException("未知的证件类型，请检查excel文件");
//        }
//        data.setCertType(certType);
//        String custName = data.getCustName();
//        if (StringUtils.isEmpty(custName)){
//            log.info("客户名称不能为空！");
//            errorList.add(data);
//            throw new ExcelAnalysisException("客户名称不能为空！");
//        }
//        String certNo = data.getCertNo();
//        if (StringUtils.isEmpty(certNo)){
//            log.info("证件号码不能为空！");
//            errorList.add(data);
//            throw new ExcelAnalysisException("证件号码不能为空！");
//        }
//        compareBean.setCompareColumn(data.getCustType() + "_" + data.getCustName());
//        custListCompare.add(compareBean);
//        excelCustList.add(data);//原始数据
//    }
//
//    @Override
//    public void doAfterAllAnalysed(AnalysisContext context) {
//        //找出excel中重复的数据
//        Map<String, List<TBCustListInfoVO>> groupMap = custListCompare.stream().collect(Collectors.groupingBy(TBCustListInfoVO::getCompareColumn));
//        List<TBCustListInfoVO> tmpErrorList = new ArrayList<>();
//        for (Map.Entry<String, List<TBCustListInfoVO>> map : groupMap.entrySet()) {
//            if (map.getValue().size() > 1) {
//                //处理业务
//                tmpErrorList.addAll(map.getValue());
//            }
//        }
//
//        //设置错误信息
//        Map<String, List<TBCustListInfoVO>> errorListVOMap = tmpErrorList.stream().collect(Collectors.groupingBy(TBCustListInfoVO::getCompareColumn));
//        errorListVOMap.entrySet().forEach(e->{
//            e.getValue().forEach(vo->{
//                vo.setIsVaild("0");
//                if (StringUtils.isBlank(vo.getErrorInfo())){
//                    vo.setErrorInfo("同一客户类型下存在客户名称相同的记录");
//                }else {
//                    vo.setErrorInfo(vo.getErrorInfo() + "," + "同一客户类型下存在客户名称相同的记录");
//                }
//
//                errorList.add(vo);
//            });
//        });
//
//        TBCustListUtil utils = new TBCustListUtil();
//        List<TBCustListInfoVO> tmpList = new ArrayList<>();
//        tmpList.addAll(errorList);
//        checkedDataList.addAll(tmpList);
//        checkedDataList.addAll(utils.getTwoListDifference(custListCompare,tmpList));
//        log.info("校验excel完成");
//    }
//
//    /**
//     * 接收invoke中的异常,将异常处理抛出到service
//     */
//    @Override
//    public void onException(Exception exception, AnalysisContext context) throws Exception {
//
//        //获取行号
//        Integer index = context.readRowHolder().getRowIndex() + 1;
//        //invoke中的异常
//        if (exception instanceof ExcelAnalysisException) {
//            ExcelAnalysisException excelAnalysisException = (ExcelAnalysisException) exception;
//            String message = excelAnalysisException.getMessage();
//            log.info(exception.getMessage());
//            throw new BizException("[数据处理异常--第" + index + "行]----" + message);
//        }
//        throw new BizException("[数据解析异常] 请检查文件");
//
//    }
//
//    @Override
//    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
//        log.info("解析到一条头数据:{}", JSONUtil.toJsonStr(headMap));
//        //根据自己的情况去做表头的判断即可
//        if(!headMap.get(0).equals("客户类型") || !headMap.get(1).equals("客户名称") ||
//                !headMap.get(2).equals("证件类型") || !headMap.get(3).equals("证件号码")){
//            throw new ExcelAnalysisStopException("您上传的文件格式与模板格式不一致，请检查后重新上传");
//        }
//    }
//
//
//}

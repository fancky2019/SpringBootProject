package com.example.demo.easyexcel.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.demo.easyexcel.GXDetailListVO;
import com.example.demo.easyexcel.ShareConstant;
import com.example.demo.utility.RedisUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Data
public class GXDetailListExcelListener extends AnalysisEventListener<GXDetailListVO> {


    List<GXDetailListVO> excelCustList = new ArrayList<>();

    List<GXDetailListVO> custListCompare = new ArrayList<>();

    List<GXDetailListVO> errorList = new ArrayList<>();

    List<GXDetailListVO> checkedDataList = new ArrayList<>();

//    private TGxDetailListMapper gxDetailListMapper = (TGxDetailListMapper)ApplicationContextHelper.getBean(TGxDetailListMapper.class);
//
//    private RedisUtil redisUtil = (RedisUtil)ApplicationContextHelper.getBean(RedisUtil.class);
//
//    private Map<Object,Object> certTypeMap = redisUtil.hmget(TongBaoConstant.TBRedisPrefix.TB_CODE_VALUE.getPrefixCode() + "certType");
//



    //没读一行，做校验
    @Override
    public void invoke(GXDetailListVO data, AnalysisContext context) {
        //对每一条数据做校验
        GXDetailListVO compareBean = new GXDetailListVO();
        String policyNo = data.getPolicyNo();
        if (StringUtils.isEmpty(policyNo)){
            log.info("保单号不能为空！");
            data.setErrorInfo("保单号不能为空！");
            errorList.add(data);
//            throw new ExcelAnalysisException("保单号不能为空！");
        }
        if(StringUtils.isNotEmpty(data.getPolicyType())){
            String policyType = data.getPolicyType();
            if(ShareConstant.PolicyType.ORIGINAL.getValue().equals(policyType)){
                data.setPolicyType(ShareConstant.PolicyType.ORIGINAL.getCode());
            }else if(ShareConstant.PolicyType.NEWEST.getValue().equals(policyType)){
                data.setPolicyType(ShareConstant.PolicyType.NEWEST.getCode());
            }else{
                data.setErrorInfo("未知的保单类型");
                errorList.add(data);
                log.info("未知的保单类型，请检查excel文件");
//                throw new ExcelAnalysisException("未知的保单类型，请检查excel文件");
            }
        }
        if(StringUtils.isEmpty(data.getDataSource())){
            log.info("数据来源不能为空！");
            data.setErrorInfo("数据来源不能为空！");
            errorList.add(data);
//            throw new ExcelAnalysisException("数据来源不能为空！");
        }
        String dataSource = data.getDataSource();
        if(ShareConstant.DataSource.P09.getValue().equals(dataSource)){
            data.setDataSource(ShareConstant.DataSource.P09.getCode());
        }else if(ShareConstant.DataSource.IMPORT.getValue().equals(dataSource)){
            data.setDataSource(ShareConstant.DataSource.IMPORT.getCode());
        }else{
            data.setErrorInfo("未知的数据来源");
            errorList.add(data);
            log.info("未知的数据来源，请检查excel文件");
//            throw new ExcelAnalysisException("未知的数据来源，请检查excel文件");
        }
        if(StringUtils.isNotEmpty(data.getAuditStatus())){
            String auditStatus = data.getAuditStatus();
            if(ShareConstant.AuditStatus.AUDIT_A.getValue().equals(auditStatus)){
                data.setAuditStatus(ShareConstant.AuditStatus.AUDIT_A.getCode());
            }else if(ShareConstant.AuditStatus.AUDIT_B.getValue().equals(auditStatus)){
                data.setAuditStatus(ShareConstant.AuditStatus.AUDIT_B.getCode());
            }else if(ShareConstant.AuditStatus.AUDIT_C.getValue().equals(auditStatus)){
                data.setAuditStatus(ShareConstant.AuditStatus.AUDIT_C.getCode());
            }else if(ShareConstant.AuditStatus.AUDIT_D.getValue().equals(auditStatus)){
                data.setAuditStatus(ShareConstant.AuditStatus.AUDIT_D.getCode());
            }else if(ShareConstant.AuditStatus.AUDIT_E.getValue().equals(auditStatus)){
                data.setAuditStatus(ShareConstant.AuditStatus.AUDIT_E.getCode());
            }else if(ShareConstant.AuditStatus.AUDIT_F.getValue().equals(auditStatus)){
                data.setAuditStatus(ShareConstant.AuditStatus.AUDIT_F.getCode());
            }else if(ShareConstant.AuditStatus.AUDIT_G.getValue().equals(auditStatus)){
                data.setAuditStatus(ShareConstant.AuditStatus.AUDIT_G.getCode());
            }else{
                data.setErrorInfo("未知的审核状态");
                errorList.add(data);
                log.info("未知的审核状态，请检查excel文件");
//                throw new ExcelAnalysisException("未知的审核状态，请检查excel文件");
            }
        }

        /*
            数据来源：P09 相关校验
        */
        if(ShareConstant.DataSource.P09.getCode().equals(data.getDataSource())) {
//            if(StringUtils.isEmpty(data.getAuditStatus())){
//                log.info("审核状态不能为空！");
//                errorList.add(data);
//                throw new ExcelAnalysisException("审核状态不能为空！");
//            }
            if (StringUtils.isEmpty(data.getDataStatus())) {
                log.info("数据状态不能为空！");
                data.setErrorInfo("数据状态不能为空！");
                errorList.add(data);
//                throw new ExcelAnalysisException("数据状态不能为空！");
            }
            if (StringUtils.isNotEmpty(data.getDataStatus())) {
                String dataStatus = data.getDataStatus();
                if (ShareConstant.DataStatus.DATA_A.getValue().equals(dataStatus)) {
                    data.setDataStatus(ShareConstant.DataStatus.DATA_A.getCode());
                } else if (ShareConstant.DataStatus.DATA_B.getValue().equals(dataStatus)) {
                    data.setDataStatus(ShareConstant.DataStatus.DATA_B.getCode());
                } else if (ShareConstant.DataStatus.DATA_C.getValue().equals(dataStatus)) {
                    data.setDataStatus(ShareConstant.DataStatus.DATA_C.getCode());
                } else {
                    log.info("数据来源P09保单数据状态有误：" + dataStatus);
                    data.setErrorInfo("数据来源P09保单数据状态有误：" + dataStatus);
                    //转换成code，存入数据库
                    if(ShareConstant.DataStatus.DATA_D.getValue().equals(dataStatus)){
                        data.setDataStatus(ShareConstant.DataStatus.DATA_D.getCode());
                    }
                    if(ShareConstant.DataStatus.DATA_E.getValue().equals(dataStatus)) {
                        data.setDataStatus(ShareConstant.DataStatus.DATA_E.getCode());
                    }
                    errorList.add(data);
//                    throw new ExcelAnalysisException("数据来源P09保单数据状态为" + data.getDataStatus() + "不能更新状态");
                }
           }

            //数据库查询P09数据
            if(StringUtils.isEmpty(data.getBranchCode()) || null == data.getEffectTime() || "".equals(data.getEffectTime())){
                data.setErrorInfo("商慧保系统中找不到该数据");
                errorList.add(data);
                log.info("商慧保系统中找不到该数据，请检查excel文件");
//                throw new ExcelAnalysisException("商慧保系统中找不到该数据，请检查excel文件");
            }
            if(StringUtils.isNotEmpty(data.getBranchCode()) && null != data.getEffectTime()){
//                QueryWrapper<TGxDetailList> queryWrapper = new QueryWrapper<>();
//                queryWrapper.eq("policy_no",data.getPolicyNo());
//                queryWrapper.eq("branch_code",data.getBranchCode());
//                queryWrapper.eq("data_source",data.getDataSource());
//                queryWrapper.eq("effect_time",data.getEffectTime());
//                TGxDetailList tGxDetailList = gxDetailListMapper.selectOne(queryWrapper);
//                if(ObjectUtils.isEmpty(tGxDetailList)){
//                    data.setErrorInfo("商慧保系统中找不到该数据");
//                    errorList.add(data);
//                    log.info("商慧保系统中找不到该数据，请检查excel文件");
////                    throw new ExcelAnalysisException("商慧保系统中找不到该数据，请检查excel文件");
//                }
            }
        }

        /*
            数据来源：调入 相关校验
        */
        if(ShareConstant.DataSource.IMPORT.getCode().equals(data.getDataSource())){
            if(StringUtils.isNotEmpty(data.getDataStatus())){
                String dataStatus = data.getDataStatus();
                if(ShareConstant.DataStatus.DATA_D.getValue().equals(dataStatus)){
                    data.setDataStatus(ShareConstant.DataStatus.DATA_D.getCode());
                }else if(ShareConstant.DataStatus.DATA_E.getValue().equals(dataStatus)){
                    data.setDataStatus(ShareConstant.DataStatus.DATA_E.getCode());
                }else{
                    log.info("数据来源:调入,保单数据状态有误："+dataStatus);
                    data.setErrorInfo("数据来源:调入,保单数据状态有误："+dataStatus);
                    //转换成code，存入数据库
                    if (ShareConstant.DataStatus.DATA_A.getValue().equals(dataStatus)) {
                        data.setDataStatus(ShareConstant.DataStatus.DATA_A.getCode());
                    }
                    if (ShareConstant.DataStatus.DATA_B.getValue().equals(dataStatus)) {
                        data.setDataStatus(ShareConstant.DataStatus.DATA_B.getCode());
                    }
                    if (ShareConstant.DataStatus.DATA_C.getValue().equals(dataStatus)) {
                        data.setDataStatus(ShareConstant.DataStatus.DATA_C.getCode());
                    }
                    errorList.add(data);
//                    throw new ExcelAnalysisException("数据来源:调入,保单数据状态为"+data.getDataStatus()+"不能更新状态");
                }
            }else{
                //除了数据来源为”调入“，且审核状态为空的保单数据，该字段可以为空，其余情况该字段为必填
                if(StringUtils.isNotEmpty(data.getAuditStatus())){
                    log.info("数据状态不能为空！");
                    data.setErrorInfo("数据状态不能为空！");
                    errorList.add(data);
//                    throw new ExcelAnalysisException("数据状态不能为空！");
                }
            }
            //TODO  如果审核状态为空，则去 P09 查询补全相关信息，保单类型默认为最新保单、数据状态默认为调入已确认、审核状态默认为未处理

        }
        //只有审核状态为未处理、未通过、退回、调出退回，才能进行更新，否则整批导入失败
        if(StringUtils.isNotEmpty(data.getAuditStatus())){
            String auditStatus = data.getAuditStatus();
            if (ShareConstant.AuditStatus.AUDIT_B.getValue().equals(data.getAuditStatus())){
                log.info("保单审核状态"+data.getAuditStatus());
                data.setErrorInfo("保单审核状态为["+data.getAuditStatus()+"]不能更新状态");
                errorList.add(data);
//                throw new ExcelAnalysisException("保单审核状态为["+data.getAuditStatus()+"]不能更新状态");
            }
            if (ShareConstant.AuditStatus.AUDIT_C.getValue().equals(data.getAuditStatus())){
                log.info("保单审核状态"+data.getAuditStatus());
                data.setErrorInfo("保单审核状态为["+data.getAuditStatus()+"]不能更新状态");
                errorList.add(data);
//                throw new ExcelAnalysisException("保单审核状态为["+data.getAuditStatus()+"]不能更新状态");
            }
            if (ShareConstant.AuditStatus.AUDIT_D.getValue().equals(data.getAuditStatus())){
                log.info("保单审核状态"+data.getAuditStatus());
                data.setErrorInfo("保单审核状态为["+data.getAuditStatus()+"]不能更新状态");
                errorList.add(data);
//                throw new ExcelAnalysisException("保单审核状态为["+data.getAuditStatus()+"]不能更新状态");
            }
            if(ShareConstant.AuditStatus.AUDIT_A.getValue().equals(auditStatus)){
                data.setAuditStatus(ShareConstant.AuditStatus.AUDIT_A.getCode());
            }
            if(ShareConstant.AuditStatus.AUDIT_E.getValue().equals(auditStatus)){
                data.setAuditStatus(ShareConstant.AuditStatus.AUDIT_E.getCode());
            }
            if(ShareConstant.AuditStatus.AUDIT_G.getValue().equals(auditStatus)){
                data.setAuditStatus(ShareConstant.AuditStatus.AUDIT_G.getCode());
            }
        }
        if(StringUtils.isNotEmpty(data.getHandleStatus())){
            if(ShareConstant.HandleStatus.UNTREATED.getValue().equals(data.getHandleStatus())){
                data.setHandleStatus(ShareConstant.HandleStatus.UNTREATED.getCode());
            }else if(ShareConstant.HandleStatus.PROCESSED.getValue().equals(data.getHandleStatus())){
                data.setHandleStatus(ShareConstant.HandleStatus.PROCESSED.getCode());
            }else{
                data.setErrorInfo("未知的处理状态");
                errorList.add(data);
                log.info("未知的处理状态，请检查excel文件");
//                throw new ExcelAnalysisException("未知的处理状态，请检查excel文件");
            }
        }
        BeanUtil.copyProperties(data,compareBean);
        custListCompare.add(compareBean);
        excelCustList.add(data);//原始数据
    }



    //读取完全部数据执行

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //找出excel中重复的数据
        Map<String, List<GXDetailListVO>> groupMap = custListCompare.stream().collect(Collectors.groupingBy(GXDetailListVO::getPolicyNo));
        List<GXDetailListVO> tmpErrorList = new ArrayList<>();
        for (Map.Entry<String, List<GXDetailListVO>> map : groupMap.entrySet()) {
            if (map.getValue().size() > 1) {
                //处理业务
                tmpErrorList.addAll(map.getValue());
            }
        }

        //设置错误信息
        Map<String, List<GXDetailListVO>> errorListVOMap = tmpErrorList.stream().collect(Collectors.groupingBy(GXDetailListVO::getPolicyNo));
        errorListVOMap.entrySet().forEach(e->{
            e.getValue().forEach(vo->{
                if (StringUtils.isBlank(vo.getErrorInfo())){
                    vo.setErrorInfo("存在保单号相同的记录");
                }else {
                    vo.setErrorInfo(vo.getErrorInfo() + "," + "存在保单号相同的记录");
                }
                errorList.add(vo);
            });
        });

//        GXDetailListUtil utils = new GXDetailListUtil();
//        List<GXDetailListVO> tmpList = new ArrayList<>();
//        tmpList.addAll(errorList);
//        checkedDataList.addAll(tmpList);
//        checkedDataList.addAll(utils.getTwoListDifference(custListCompare,tmpList));
        log.info("校验excel完成");
    }

    /**
     * 接收invoke中的异常,将异常处理抛出到service
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {

        //获取行号
        Integer index = context.readRowHolder().getRowIndex() + 1;
        //invoke中的异常
        try{
            if (exception instanceof ExcelAnalysisException) {
                ExcelAnalysisException excelAnalysisException = (ExcelAnalysisException) exception;
                String message = excelAnalysisException.getMessage();
                log.info(exception.getMessage());
                log.info("[数据处理异常--第" + index + "行]----" + message);
//                throw new BizException("[数据处理异常--第" + index + "行]----" + message);
            }
            log.info("[数据解析异常] 请检查文件 {}",exception.getMessage());
//            throw new BizException("[数据解析异常] 请检查文件"+exception.getMessage());
        }catch (Exception e){
            log.info("[数据解析异常] 请检查文件 {}",exception.getMessage());
        }


    }

    //列头
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JSONUtil.toJsonStr(headMap));
        //根据自己的情况去做表头的判断即可
//        if(!headMap.get(0).equals("客户类型") || !headMap.get(1).equals("客户名称") ||
//                !headMap.get(2).equals("证件类型") || !headMap.get(3).equals("证件号码")){
//            throw new ExcelAnalysisStopException("您上传的文件格式与模板格式不一致，请检查后重新上传");
//        }
    }


}

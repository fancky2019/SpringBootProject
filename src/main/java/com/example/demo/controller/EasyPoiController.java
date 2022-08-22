//package com.example.demo.controller;
//
//import cn.afterturn.easypoi.excel.ExcelExportUtil;
//import cn.afterturn.easypoi.excel.ExcelImportUtil;
//import cn.afterturn.easypoi.excel.entity.ExportParams;
//import cn.afterturn.easypoi.excel.entity.ImportParams;
//import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
//import com.example.demo.model.pojo.EasyPoiPojo;
//import com.example.demo.model.viewModel.MessageResult;
//import com.example.demo.model.viewModel.Person;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@RestController
//@RequestMapping("/easyPoi")
//public class EasyPoiController {
//    private static Logger logger = LogManager.getLogger(EasyPoiController.class);
//
//    @GetMapping("/exportData")
//    public void exportData(EasyPoiPojo query,HttpServletResponse response) {
//        try {
//            List<EasyPoiPojo> list = new ArrayList<>();
//            EasyPoiPojo easyPoiPojo1 = new EasyPoiPojo(1, "fancky1", 25, "chenguxyuan", "anhui");
//            EasyPoiPojo easyPoiPojo2 = new EasyPoiPojo(2, "fancky2", 25, "农民", "anhui");
//            list.add(easyPoiPojo1);
//            list.add(easyPoiPojo2);
//            //不设置参数
////            ExportParams exportParams = new ExportParams("title", "sheetName");
//
//            ExportParams exportParams = new ExportParams();
//            exportParams.setSheetName("sheetName");//设置SheetName
//            exportParams.setType(ExcelType.XSSF);//文件后缀为.xlsx。不设置默认为 HSSF-->xls
//            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, EasyPoiPojo.class, list);
//
//            String fileName = "filename-EasyPoiPojo导出.xlsx";
////            String fileNameExportParamsWithOutType = "个人信息filename.xls";
//            response.setCharacterEncoding("UTF-8");
//            response.setHeader("Content-Type", "application/vnd.ms-excel");
//            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
//            workbook.write(response.getOutputStream());
//
//            String contentType= response.getHeader("Content-Type");
//            int m=0;
//        } catch (Exception ex) {
//
//        }
//
//    }
//
//    @RequestMapping(value = "/importData", method = RequestMethod.POST)
//    public MessageResult<Void> importData(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request) {
//        MessageResult<Void> messageResult = new MessageResult<>();
//        try {
//
//            //region  保存上传的文件
////            LocalDateTime localDateTime = LocalDateTime.now();
////            String dateStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
////            String rootPath = System.getProperty("user.dir");
//////            String rootPath =   ResourceUtils.getURL("classpath:").getPath();
////            String directory = rootPath + "\\uploadfiles" + "\\" + dateStr + "\\";
////            File destFile = new File(directory);
////            //判断路径是否存在,和C#不一样。她判断路径和文件是否存在
////            if (!destFile.exists()) {
////                destFile.mkdirs();
////            }
////
////            //获取body中的参数
//////            String value = (String)request.getAttribute("paramName");
////            //获取文件名称
////            String sourceFileName = file.getOriginalFilename();
////            //写入目的文件
////            String fileFullName = directory + sourceFileName;
////            File existFile=  new File(fileFullName);
////            if(existFile.exists())
////            {
////                existFile.delete();
////            }
////            file.transferTo(existFile);
//
//            //  messageResult = eosOrderService.importData(fileFullName);
////endregion
//
//            ImportParams importParams = new ImportParams();
//            // params.setHeadRows(2);//有标题，跳过2行=标题行+列行
//            importParams.setHeadRows(1);//跳过列行
//
//            // params.setTitleRows(0);
//            List<EasyPoiPojo> result = ExcelImportUtil.importExcel(file.getInputStream(), EasyPoiPojo.class, importParams);
//            int m = 0;
//        } catch (Exception e) {
//
//            logger.error(e.toString());
//        }
//        return messageResult;
//    }
//}

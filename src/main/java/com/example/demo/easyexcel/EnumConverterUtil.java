package com.example.demo.easyexcel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.example.demo.model.vo.EnumAnnotation;
import com.example.demo.model.vo.EnumOperation;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EnumConverterUtil implements Converter<Integer> {


    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) {
        // this.convertToJavaData( context.getAnalysisContext().currentReadHolder().globalConfiguration());
        ReadCellData<?> cellData = context.getReadCellData();
        ExcelContentProperty contentProperty = context.getContentProperty();
        String cellStringValue = cellData.getStringValue();
        Field field = contentProperty.getField();
        EnumAnnotation enumAnnotation = field.getAnnotation(EnumAnnotation.class);
        if (enumAnnotation == null) {
            return 0;
        }
        Class<?> enumClass = enumAnnotation.enumClass();
        // 获取所有枚举常量
        Object[] enumConstants = enumClass.getEnumConstants();
        EnumOperation[] enumOperations = (EnumOperation[]) enumConstants;
        int value = 0;
        for (EnumOperation enumOperation : enumOperations) {
            if (enumOperation.getDescription().equals(cellStringValue)) {
                value = enumOperation.getValue();
                break;
            }
        }
        return value;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) {
        // 导出操作把整数类型转换为字符串类型
        Integer cellData = context.getValue();
        ExcelContentProperty contentProperty = context.getContentProperty();
        Field field = contentProperty.getField();
        EnumAnnotation enumAnnotation = field.getAnnotation(EnumAnnotation.class);
        if (enumAnnotation == null) {
            return new WriteCellData<>();
        }
        // 解析枚举字符串
        Class<?> enumClass = enumAnnotation.enumClass();
        // 获取所有枚举常量
        Object[] enumConstants = enumClass.getEnumConstants();
        EnumOperation[] enumOperations = (EnumOperation[]) enumConstants;
        HashMap<Integer, String> hashMap = new HashMap<>(16);
        for (EnumOperation enumOperation : enumOperations) {
            hashMap.put(enumOperation.getValue(), enumOperation.getDescription());
        }
        return new WriteCellData<>(hashMap.get(cellData));
    }


}

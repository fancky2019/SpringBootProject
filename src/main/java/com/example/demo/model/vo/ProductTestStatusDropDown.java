package com.example.demo.model.vo;

import com.example.demo.easyexcel.DropDownSetInterface;
import com.example.demo.easyexcel.ShareConstant;

public class ProductTestStatusDropDown implements DropDownSetInterface {
    @Override
    public String[] getSource() {
        return new String[]{
                ProductTestStatusEnum.EXCEPTION.getDescription(),
                ProductTestStatusEnum.NORMAL.getDescription(),
                ProductTestStatusEnum.INVALID.getDescription()
        };
    }
}

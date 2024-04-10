package com.example.demo.easyexcel;

public class DataSourceDropDownSetImpl implements DropDownSetInterface {
    @Override
    public String[] getSource() {
        return new String[]{
                ShareConstant.DataSource.P09.getValue(),
                ShareConstant.DataSource.IMPORT.getValue()
        };
    }
}

package com.example.demo.model.viewModel;



//import com.sun.org.glassfish.gmbal.Description;

import java.io.Serializable;

public class Person implements Serializable {
    // @SuppressWarnings("")
//    @Description("姓名")
    private String name;
//    @Description("年龄")
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
        //  System.out.println("setAgefddffddf");
    }

    public Person() {

    }

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

//    @Override
//    public  String toString()
//    {
//        return "name:"+name +" age:"+age==null?"":age.toString();
//    }

}

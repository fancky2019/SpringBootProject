package com.example.demo.model.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Auther fancky
 * @Date 2020-10-26 9:50
 * @Description
 */
@Setter
@Getter
//@ToString
public class  LomBokSubOne extends LomBokSuper
{
  private  String JobNameOne;

  @Override
  public  String toString()
  {
      return  JobNameOne;
  }
}
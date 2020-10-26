package com.example.demo.controller;

import com.example.demo.model.entity.wms.Order;
import com.example.demo.model.pojo.LomBokSubOne;
import com.example.demo.model.pojo.LomBokSubTwo;
import com.example.demo.model.viewModel.MessageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther fancky
 * @Date 2020-10-26 9:48
 * @Description
 */

@RestController
@RequestMapping("/lombox")
public class LomBokController {

    @GetMapping("")
    public MessageResult<Void> LomBoxTest() {
        try {
            LomBokSubOne lomBokSubOne = new LomBokSubOne();
            String s1 = lomBokSubOne.toString();


            LomBokSubTwo lomBokSubTwo = new LomBokSubTwo();
            lomBokSubTwo.setName("li");
            int h1 = lomBokSubTwo.hashCode();
            LomBokSubTwo lomBokSubTwo1 = new LomBokSubTwo();
            lomBokSubTwo1.setName("li1");
            int h2 = lomBokSubTwo1.hashCode();


            //注释@EqualsAndHashCode(callSuper = true)，true;启用false
            boolean eq=lomBokSubTwo.equals(lomBokSubTwo1);

            boolean re=lomBokSubTwo==lomBokSubTwo1;
            if(lomBokSubTwo==lomBokSubTwo1)
            {

            }

            Order order1 =new Order();
            Order order2 =new Order();

            int ho1=order1.hashCode();
            int ho2=order2.hashCode();
            int m = 0;
            // kafkaTest.test();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}

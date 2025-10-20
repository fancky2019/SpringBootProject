package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.entity.wms.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

//    @Before
//    public void setup() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//    }


    //post  测试  ：get测试 见UserControllerTest
    @Test
    public void updateProduct() {
        try {
            Product product = new Product();
            product.setId(48);
            product.setProductname("productNameTest");
            product.setPrice(new BigDecimal(10));

            String requestJson = JSONObject.toJSONString(product);
            MvcResult mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.post("/product/updateProduct")
                            .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = mvcResult.getResponse().getContentAsString();
            System.out.println("输出 :" + responseString);
        } catch (Exception ex) {
        }
    }
}

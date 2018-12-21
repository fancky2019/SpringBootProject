package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }


    //get 测试，  post  测试见ProductControllerTest
    @Test
    public void getUser() {
        try {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/user/getUser").accept(MediaType.APPLICATION_JSON)
                    .param("id", "1"))
//                    .param("date","2017-7-18 00:00:00")
//                    .param("pageNum","1")
//                    .param("pageSize","10"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();
            System.out.println("输出 :" + mvcResult.getResponse().getContentAsString());
        } catch (Exception ex) {
        }
    }


}

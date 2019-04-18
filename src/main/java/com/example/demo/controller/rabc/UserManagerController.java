package com.example.demo.controller.rabc;

import com.example.demo.controller.UserController;
import com.example.demo.model.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/*
RABC全向管理项目
Swagger: http://localhost:8080/swagger-ui.html
 */
@Controller
@RequestMapping("/userManager")
public class UserManagerController {

    private static Logger logger = LogManager.getLogger(UserController.class);

    @GetMapping("/getUser")
    @ResponseBody//当使用@Controller返回数据必须要加上@ResponseBody
    public User getUser(User user) {
      //  User user = new User();
        user.setAccount("test");
        return user;


//        logger.info("dssdsdsd");
//        logger.error("dssdsdsd");
//      //  User re= userService.selectByPrimaryKey(user.getId());
//        return ;
    }

    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addUser")
    @ResponseBody
    public void addUser(@RequestBody User user) {
        try {
            int a = user.getId();
        } catch (Exception ex) {
            String s = ex.getMessage();
        }
    }
}

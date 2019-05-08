package com.example.demo.controller.rabc;

import com.example.demo.controller.UserController;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import com.example.demo.service.UserService;
import com.example.demo.service.rabc.UserManagerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/*
RABC权限管理项目
Swagger: http://localhost:8080/swagger-ui.html
 */
@RestController
@RequestMapping("rabc/userManager")
public class UserManagerController {

    private static Logger logger = LogManager.getLogger(UserController.class);
    @Resource
    private UserManagerService userManagerService;


    @GetMapping("/getUser")
    public MessageResult<Users> getUser(Integer id) {
        MessageResult<Users> message = new MessageResult<>();
        try {
            message = userManagerService.selectByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageDataWithCount", method = RequestMethod.GET)
    public MessageResult<PageData<UsersVM>> getPageDataWithCount(UsersVM viewModel) {
        MessageResult<PageData<UsersVM>> message = new MessageResult<>();
        try {
            message = userManagerService.getPageDataWithCount(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageData", method = RequestMethod.GET)
    public MessageResult<List<UsersVM>> getPageData(UsersVM viewModel) {
        MessageResult<List<UsersVM>> message = new MessageResult<>();
        try {
            message = userManagerService.getPageData(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addUser")
    public MessageResult<Void> addUser(@RequestBody Users user) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = userManagerService.addUser(user);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    //如果不指定方法，会出现;GET、HEAD、POST、PUT、DELETE、OPTIONS、PATCH方法。
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public MessageResult<Void> deleteUser(@RequestBody Integer id) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = userManagerService.deleteByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    @PostMapping("/batchDeleteByID")
    public MessageResult<Void> batchDeleteByID(@RequestBody List<Integer> list) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = userManagerService.batchDeleteByID(list);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    @PostMapping("/updateUser")
    public MessageResult<Void> updateUser(@RequestBody Users user) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = userManagerService.updateByPrimaryKey(user);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }
}

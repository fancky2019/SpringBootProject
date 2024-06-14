package com.example.demo.controller.rabc;

import com.example.demo.model.entity.rabc.UserRoles;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.pojo.Page;
import com.example.demo.model.viewModel.rabc.UserRolesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import com.example.demo.service.rabc.RoleManagerService;
import com.example.demo.service.rabc.UserRolesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("rabc/userRoles")
public class UserRolesManagerController {

    private static Logger logger = LogManager.getLogger(UserRolesManagerController.class);
    @Resource
    private UserRolesService userRolesService;

    @GetMapping("/getUserRoles")
    public MessageResult<UserRoles> getUserRoles(Integer id) {
        MessageResult<UserRoles> message = new MessageResult<>();
        try {
            message = userRolesService.selectByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageDataWithCount", method = RequestMethod.GET)
    public MessageResult<PageData<UserRolesVM>> getPageDataWithCount(UserRolesVM viewModel) {
        MessageResult<PageData<UserRolesVM>> message = new MessageResult<>();
        try {
            message = userRolesService.getPageDataWithCount(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageData", method = RequestMethod.GET)
    public MessageResult<List<UserRolesVM>> getPageData(UserRolesVM viewModel) {
        MessageResult<List<UserRolesVM>> message = new MessageResult<>();
        try {
            message = userRolesService.getPageData(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addUserRoles")
    public MessageResult<Void> addUserRoles(@RequestBody UserRoles userRoles) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = userRolesService.addUserRoles(userRoles);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    //如果不指定方法，会出现;GET、HEAD、POST、PUT、DELETE、OPTIONS、PATCH方法。
    @RequestMapping(value = "/deleteUserRoles", method = RequestMethod.POST)
    public MessageResult<Void> deleteUserRoles(@RequestBody Integer id) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = userRolesService.deleteByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    @PostMapping("/updateUserRoles")
    public MessageResult<Void> updateUserRoles(@RequestBody UserRoles userRoles) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = userRolesService.updateByPrimaryKey(userRoles);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }
}

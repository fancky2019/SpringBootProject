package com.example.demo.controller.rabc;

import com.example.demo.controller.UserController;
import com.example.demo.model.entity.rabc.RoleMenus;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.rabc.RoleMenusVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import com.example.demo.service.rabc.RoleMenusService;
import com.example.demo.service.rabc.UserManagerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("rabc/roleMenus")
public class RoleMenusManagerController {
    private static Logger logger = LogManager.getLogger(RoleMenusManagerController.class);
    @Resource
    private RoleMenusService  roleMenusService;


    @GetMapping("/getRoleMenu")
    public MessageResult<RoleMenus> getRoleMenu(Integer id) {
        MessageResult<RoleMenus> message = new MessageResult<>();
        try {
            message = roleMenusService.selectByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageDataWithCount", method = RequestMethod.GET)
    public MessageResult<PageData<RoleMenusVM>> getPageDataWithCount(RoleMenusVM viewModel) {
        MessageResult<PageData<RoleMenusVM>> message = new MessageResult<>();
        try {
            message = roleMenusService.getPageDataWithCount(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageData", method = RequestMethod.GET)
    public MessageResult<List<RoleMenusVM>> getPageData(RoleMenusVM viewModel) {
        MessageResult<List<RoleMenusVM>> message = new MessageResult<>();
        try {
            message = roleMenusService.getPageData(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addRoleMenu")
    public MessageResult<Void> addRoleMenu(@RequestBody RoleMenus roleMenu) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = roleMenusService.addRoleMenu(roleMenu);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    //如果不指定方法，会出现;GET、HEAD、POST、PUT、DELETE、OPTIONS、PATCH方法。
    @RequestMapping(value = "/deleteRoleMenu", method = RequestMethod.POST)
    public MessageResult<Void> deleteRoleMenu(@RequestBody Integer id) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = roleMenusService.deleteByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    @PostMapping("/updateRoleMenu")
    public MessageResult<Void> updateRoleMenu(@RequestBody RoleMenus roleMenu) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = roleMenusService.updateByPrimaryKey(roleMenu);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }
}

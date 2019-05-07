package com.example.demo.controller.rabc;

import com.example.demo.model.entity.rabc.Roles;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.rabc.RolesVM;
import com.example.demo.service.rabc.RoleManagerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("rabc/role")
public class RoleManagerController {
    private static Logger logger = LogManager.getLogger(RoleManagerController.class);
    @Resource
    private RoleManagerService roleManagerService;

    @GetMapping("/getUser")
    public MessageResult<Roles> getUser(Integer id) {
        MessageResult<Roles> message = new MessageResult<>();
        try {
            message = roleManagerService.selectByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageDataWithCount", method = RequestMethod.GET)
    public MessageResult<PageData<RolesVM>> getPageDataWithCount(RolesVM viewModel) {
        MessageResult<PageData<RolesVM>> message = new MessageResult<>();
        try {
            message = roleManagerService.getPageDataWithCount(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageData", method = RequestMethod.GET)
    public MessageResult<List<RolesVM>> getPageData(RolesVM viewModel) {
        MessageResult<List<RolesVM>> message = new MessageResult<>();
        try {
            message = roleManagerService.getPageData(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addRoles")
    public MessageResult<Void> addRoles(@RequestBody Roles roles) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = roleManagerService.addRoles(roles);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    //如果不指定方法，会出现;GET、HEAD、POST、PUT、DELETE、OPTIONS、PATCH方法。
    @RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
    public MessageResult<Void> deleteRole(@RequestBody Integer id) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = roleManagerService.deleteByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    @PostMapping("/updateRole")
    public MessageResult<Void> updateRole(@RequestBody Roles roles) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = roleManagerService.updateByPrimaryKey(roles);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

}

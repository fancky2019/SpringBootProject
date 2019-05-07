package com.example.demo.controller.rabc;

import com.example.demo.controller.UserController;
import com.example.demo.model.entity.rabc.RoleMenuAuthorities;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.rabc.RoleMenuAuthoritiesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import com.example.demo.service.rabc.RoleMenuAuthoritiesService;
import com.example.demo.service.rabc.UserManagerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("rabc/roleMenuAuthorities")
public class RoleMenuAuthoritiesManagerController {
    private static Logger logger = LogManager.getLogger(RoleMenuAuthoritiesManagerController.class);
    @Resource
    private RoleMenuAuthoritiesService roleMenuAuthoritiesService;


    @GetMapping("/getRoleMenuAuthority")
    public MessageResult<RoleMenuAuthorities> getRoleMenuAuthority(Integer id) {
        MessageResult<RoleMenuAuthorities> message = new MessageResult<>();
        try {
            message = roleMenuAuthoritiesService.selectByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageDataWithCount", method = RequestMethod.GET)
    public MessageResult<PageData<RoleMenuAuthoritiesVM>> getPageDataWithCount(RoleMenuAuthoritiesVM viewModel) {
        MessageResult<PageData<RoleMenuAuthoritiesVM>> message = new MessageResult<>();
        try {
            message = roleMenuAuthoritiesService.getPageDataWithCount(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageData", method = RequestMethod.GET)
    public MessageResult<List<RoleMenuAuthoritiesVM>> getPageData(RoleMenuAuthoritiesVM viewModel) {
        MessageResult<List<RoleMenuAuthoritiesVM>> message = new MessageResult<>();
        try {
            message = roleMenuAuthoritiesService.getPageData(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addRoleMenuAuthority")
    public MessageResult<Void> addRoleMenuAuthority(@RequestBody RoleMenuAuthorities roleMenuAuthority) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = roleMenuAuthoritiesService.addRoleMenuAuthority(roleMenuAuthority);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    //如果不指定方法，会出现;GET、HEAD、POST、PUT、DELETE、OPTIONS、PATCH方法。
    @RequestMapping(value = "/deleteRoleMenuAuthority", method = RequestMethod.POST)
    public MessageResult<Void> deleteRoleMenuAuthority(@RequestBody Integer id) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = roleMenuAuthoritiesService.deleteByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    @PostMapping("/updateRoleMenuAuthority")
    public MessageResult<Void> updateRoleMenuAuthority(@RequestBody RoleMenuAuthorities roleMenuAuthority) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = roleMenuAuthoritiesService.updateByPrimaryKey(roleMenuAuthority);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }
}

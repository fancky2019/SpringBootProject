package com.example.demo.controller.rabc;

import com.example.demo.controller.UserController;
import com.example.demo.model.entity.rabc.Authorities;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.rabc.AuthoritiesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import com.example.demo.service.rabc.AuthoritiesService;
import com.example.demo.service.rabc.UserManagerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("rabc/authority")
public class AuthorityMangerController {
    private static Logger logger = LogManager.getLogger(AuthorityMangerController.class);
    @Resource
    private AuthoritiesService authoritiesService;


    @GetMapping("/getAuthority")
    public MessageResult<Authorities> getAuthority(Integer id) {
        MessageResult<Authorities> message = new MessageResult<>();
        try {
            message = authoritiesService.selectByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageDataWithCount", method = RequestMethod.GET)
    public MessageResult<PageData<AuthoritiesVM>> getPageDataWithCount(AuthoritiesVM viewModel) {
        MessageResult<PageData<AuthoritiesVM>> message = new MessageResult<>();
        try {
            message = authoritiesService.getPageDataWithCount(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageData", method = RequestMethod.GET)
    public MessageResult<List<AuthoritiesVM>> getPageData(AuthoritiesVM viewModel) {
        MessageResult<List<AuthoritiesVM>> message = new MessageResult<>();
        try {
            message = authoritiesService.getPageData(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addAuthority")
    public MessageResult<Void> addAuthority(@RequestBody Authorities authority) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = authoritiesService.addAuthority(authority);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    //如果不指定方法，会出现;GET、HEAD、POST、PUT、DELETE、OPTIONS、PATCH方法。
    @RequestMapping(value = "/deleteAuthority", method = RequestMethod.POST)
    public MessageResult<Void> deleteAuthority(@RequestBody Integer id) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = authoritiesService.deleteByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    @PostMapping("/updateAuthority")
    public MessageResult<Void> updateAuthority(@RequestBody Authorities authority) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = authoritiesService.updateByPrimaryKey(authority);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }
}

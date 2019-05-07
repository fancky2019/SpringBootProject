package com.example.demo.controller.rabc;

import com.example.demo.controller.UserController;
import com.example.demo.model.entity.rabc.Menus;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.rabc.MenusVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import com.example.demo.service.rabc.MenusService;
import com.example.demo.service.rabc.UserManagerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("rabc/menu")
public class MenuManagerController {
    private static Logger logger = LogManager.getLogger(UserController.class);
    @Resource
    private MenusService menusService;


    @GetMapping("/getMenu")
    public MessageResult<Menus> getMenu(Integer id) {
        MessageResult<Menus> message = new MessageResult<>();
        try {
            message = menusService.selectByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageDataWithCount", method = RequestMethod.GET)
    public MessageResult<PageData<MenusVM>> getPageDataWithCount(MenusVM viewModel) {
        MessageResult<PageData<MenusVM>> message = new MessageResult<>();
        try {
            message = menusService.getPageDataWithCount(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    @RequestMapping(value = "/getPageData", method = RequestMethod.GET)
    public MessageResult<List<MenusVM>> getPageData(MenusVM viewModel) {
        MessageResult<List<MenusVM>> message = new MessageResult<>();
        try {
            message = menusService.getPageData(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addMenu")
    public MessageResult<Void> addMenu(@RequestBody Menus menu) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = menusService.addMenus(menu);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    //如果不指定方法，会出现;GET、HEAD、POST、PUT、DELETE、OPTIONS、PATCH方法。
    @RequestMapping(value = "/deleteMenu", method = RequestMethod.POST)
    public MessageResult<Void> deleteMenu(@RequestBody Integer id) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = menusService.deleteByPrimaryKey(id);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }

    @PostMapping("/updateMenu")
    public MessageResult<Void> updateMenu(@RequestBody Menus menu) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            message = menusService.updateByPrimaryKey(menu);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
            logger.error(e.toString());
        } finally {
            return message;
        }
    }
}

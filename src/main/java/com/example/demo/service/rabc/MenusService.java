package com.example.demo.service.rabc;

import com.example.demo.dao.rabc.MenusMapper;
import com.example.demo.dao.rabc.UserRolesMapper;
import com.example.demo.model.entity.rabc.Menus;
import com.example.demo.model.entity.rabc.UserRoles;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.pojo.Page;
import com.example.demo.model.viewModel.rabc.MenusVM;
import com.example.demo.model.viewModel.rabc.UserRolesVM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenusService {
    private static Logger logger = LogManager.getLogger(MenusService.class);
    @Autowired
    MenusMapper menusMapper;

    public MessageResult<Menus> selectByPrimaryKey(int id) {
        MessageResult<Menus> messageResult = new MessageResult<>();
        try {
            Menus menus = menusMapper.selectByPrimaryKey(id);
//          List<Users> list= new LinkedList<>();
//            list.add(users);
            messageResult.setData(menus);
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> addMenus(Menus menus) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = menusMapper.insert(menus);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> updateByPrimaryKey(Menus menus) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = menusMapper.updateByPrimaryKey(menus);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> deleteByPrimaryKey(Integer id) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = menusMapper.deleteByPrimaryKey(id);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<PageData<MenusVM>> getPageDataWithCount(MenusVM viewModel) {
        MessageResult<PageData<MenusVM>> message = new MessageResult<>();
        try {
            PageData<MenusVM> paegData = new PageData<>();
            Long count = menusMapper.getPageDataCount(viewModel);
            paegData.setCount(count);
            List<MenusVM> list = menusMapper.getPageData(viewModel);
            paegData.setData(list);
            message.setData(paegData);
            message.setSuccess(true);
        } catch (Exception ex) {
            message.setSuccess(false);
            message.setMessage(ex.getMessage());
            logger.error(ex.toString());
        } finally {
            return message;
        }
    }

    public MessageResult<List<MenusVM>> getPageData(MenusVM viewModel) {
        MessageResult<List<MenusVM>> message = new MessageResult<>();
        try {
            List<MenusVM> list = menusMapper.getPageData(viewModel);
            message.setData(list);
            message.setSuccess(true);
        } catch (Exception ex) {
            message.setSuccess(false);
            message.setMessage(ex.getMessage());
            logger.error(ex.toString());
        } finally {
            return message;
        }
    }
}

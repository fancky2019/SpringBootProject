package com.example.demo.service.rabc;

import com.example.demo.dao.rabc.RoleMenusMapper;
import com.example.demo.dao.rabc.UsersMapper;
import com.example.demo.model.entity.rabc.RoleMenus;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.rabc.RoleMenusVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleMenusService {
    private static Logger logger = LogManager.getLogger(RoleMenusService.class);

    @Autowired
    RoleMenusMapper roleMenusMapper;

    public MessageResult<RoleMenus> selectByPrimaryKey(int id) {
        MessageResult<RoleMenus> messageResult = new MessageResult<>();
        try {
            RoleMenus roleMenus = roleMenusMapper.selectByPrimaryKey(id);
//          List<Users> list= new LinkedList<>();
//            list.add(users);
            messageResult.setData(roleMenus);
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> addRoleMenu(RoleMenus roleMenus) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = roleMenusMapper.insert(roleMenus);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> updateByPrimaryKey(RoleMenus roleMenus) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = roleMenusMapper.updateByPrimaryKey(roleMenus);
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
            Integer result = roleMenusMapper.deleteByPrimaryKey(id);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<PageData<RoleMenusVM>> getPageDataWithCount(RoleMenusVM viewModel) {
        MessageResult<PageData<RoleMenusVM>> message = new MessageResult<>();
        try {
            PageData<RoleMenusVM> paegData=new PageData<>() ;
            Integer count= roleMenusMapper.getPageDataCount(viewModel);
            paegData.setCount(count);
            List<RoleMenusVM> list = roleMenusMapper.getPageData(viewModel);
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

    public MessageResult<List<RoleMenusVM>> getPageData(RoleMenusVM viewModel) {
        MessageResult<List<RoleMenusVM>> message = new MessageResult<>();
        try {
            List<RoleMenusVM> list = roleMenusMapper.getPageData(viewModel);
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

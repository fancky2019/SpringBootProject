package com.example.demo.service.rabc;

import com.example.demo.dao.rabc.RolesMapper;
import com.example.demo.dao.rabc.UserRolesMapper;
import com.example.demo.model.entity.rabc.Roles;
import com.example.demo.model.entity.rabc.UserRoles;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.pojo.Page;
import com.example.demo.model.viewModel.rabc.RolesVM;
import com.example.demo.model.viewModel.rabc.UserRolesVM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRolesService {
    private static Logger logger = LogManager.getLogger(UserRolesService.class);
    @Autowired
    UserRolesMapper userRolesMapper;

    public MessageResult<UserRoles> selectByPrimaryKey(int id) {
        MessageResult<UserRoles> messageResult = new MessageResult<>();
        try {
            UserRoles userRoles = userRolesMapper.selectByPrimaryKey(id);
//          List<Users> list= new LinkedList<>();
//            list.add(users);
            messageResult.setData(userRoles);
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> addUserRoles(UserRoles userRoles) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = userRolesMapper.insert(userRoles);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> updateByPrimaryKey(UserRoles userRoles) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = userRolesMapper.updateByPrimaryKey(userRoles);
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
            Integer result = userRolesMapper.deleteByPrimaryKey(id);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<PageData<UserRolesVM>> getPageDataWithCount(UserRolesVM viewModel) {
        MessageResult<PageData<UserRolesVM>> message = new MessageResult<>();
        try {
            PageData<UserRolesVM> paegData = new PageData<>();
            Long count =  userRolesMapper.getPageDataCount(viewModel);
            paegData.setCount(count);
            List<UserRolesVM> list = userRolesMapper.getPageData(viewModel);
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

    public MessageResult<List<UserRolesVM>> getPageData(UserRolesVM viewModel) {
        MessageResult<List<UserRolesVM>> message = new MessageResult<>();
        try {
            List<UserRolesVM> list = userRolesMapper.getPageData(viewModel);
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

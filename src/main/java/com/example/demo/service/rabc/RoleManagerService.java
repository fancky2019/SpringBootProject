package com.example.demo.service.rabc;

import com.example.demo.dao.rabc.RolesMapper;
import com.example.demo.dao.rabc.UsersMapper;
import com.example.demo.model.entity.rabc.Roles;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.rabc.RolesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleManagerService {
    private static Logger logger = LogManager.getLogger(UserManagerService.class);
    @Autowired
    RolesMapper rolesMapper;

    public MessageResult<Roles> selectByPrimaryKey(int id) {
        MessageResult<Roles> messageResult = new MessageResult<>();
        try {
            Roles roles = rolesMapper.selectByPrimaryKey(id);
//          List<Users> list= new LinkedList<>();
//            list.add(users);
            messageResult.setData(roles);
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> addRoles(Roles roles) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = rolesMapper.insert(roles);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> updateByPrimaryKey(Roles roles) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = rolesMapper.updateByPrimaryKey(roles);
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
            Integer result = rolesMapper.deleteByPrimaryKey(id);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<PageData<RolesVM>> getPageDataWithCount(RolesVM viewModel) {
        MessageResult<PageData<RolesVM>> message = new MessageResult<>();
        try {
            PageData<RolesVM> paegData = new PageData<>();
            Long count = rolesMapper.getPageDataCount(viewModel);
            paegData.setCount(count);
            List<RolesVM> list = rolesMapper.getPageData(viewModel);
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

    public MessageResult<List<RolesVM>> getPageData(RolesVM viewModel) {
        MessageResult<List<RolesVM>> message = new MessageResult<>();
        try {
            List<RolesVM> list = rolesMapper.getPageData(viewModel);
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

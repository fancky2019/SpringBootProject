package com.example.demo.service.rabc;

import com.example.demo.dao.rabc.UsersMapper;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.pojo.Page;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class UserManagerService {
    private static Logger logger = LogManager.getLogger(UserManagerService.class);

    @Autowired
    UsersMapper usersMapper;

    public MessageResult<Users> selectByPrimaryKey(int id) {
        MessageResult<Users> messageResult = new MessageResult<>();
        try {
            Users users = usersMapper.selectByPrimaryKey(id);
//          List<Users> list= new LinkedList<>();
//            list.add(users);
            messageResult.setData(users);
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> addUser(Users user) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = usersMapper.insert(user);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> updateByPrimaryKey(Users user) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = usersMapper.updateByPrimaryKey(user);
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
            Integer result = usersMapper.deleteByPrimaryKey(id);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> batchDeleteByID(List<Integer> list) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = usersMapper.batchDeleteByID(list);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<PageData<UsersVM>> getPageDataWithCount(UsersVM viewModel) {
        MessageResult<PageData<UsersVM>> message = new MessageResult<>();
        try {
            PageData<UsersVM> paegData = new PageData<>();
            Long count = usersMapper.getPageDataCount(viewModel);
            paegData.setCount(count);
            List<UsersVM> list = usersMapper.getPageData(viewModel);
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

    public MessageResult<List<UsersVM>> getPageData(UsersVM viewModel) {
        MessageResult<List<UsersVM>> message = new MessageResult<>();
        try {
            List<UsersVM> list = usersMapper.getPageData(viewModel);
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

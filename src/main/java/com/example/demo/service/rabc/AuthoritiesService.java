package com.example.demo.service.rabc;

import com.example.demo.dao.rabc.AuthoritiesMapper;
import com.example.demo.dao.rabc.UserRolesMapper;
import com.example.demo.model.entity.rabc.Authorities;
import com.example.demo.model.entity.rabc.UserRoles;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.rabc.AuthoritiesVM;
import com.example.demo.model.viewModel.rabc.UserRolesVM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthoritiesService {
    private static Logger logger = LogManager.getLogger(AuthoritiesService.class);
    @Autowired
    AuthoritiesMapper authoritiesMapper;

    public MessageResult<Authorities> selectByPrimaryKey(int id) {
        MessageResult<Authorities> messageResult = new MessageResult<>();
        try {
            Authorities authority = authoritiesMapper.selectByPrimaryKey(id);
//          List<Users> list= new LinkedList<>();
//            list.add(users);
            messageResult.setData(authority);
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> addAuthority(Authorities authority) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = authoritiesMapper.insert(authority);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> updateByPrimaryKey(Authorities authority) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = authoritiesMapper.updateByPrimaryKey(authority);
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
            Integer result = authoritiesMapper.deleteByPrimaryKey(id);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<PageData<AuthoritiesVM>> getPageDataWithCount(AuthoritiesVM viewModel) {
        MessageResult<PageData<AuthoritiesVM>> message = new MessageResult<>();
        try {
            PageData<AuthoritiesVM> paegData=new PageData<>() ;
            Integer count= authoritiesMapper.getPageDataCount(viewModel);
            paegData.setCount(count);
            List<AuthoritiesVM> list = authoritiesMapper.getPageData(viewModel);
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

    public MessageResult<List<AuthoritiesVM>> getPageData(AuthoritiesVM viewModel) {
        MessageResult<List<AuthoritiesVM>> message = new MessageResult<>();
        try {
            List<AuthoritiesVM> list = authoritiesMapper.getPageData(viewModel);
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

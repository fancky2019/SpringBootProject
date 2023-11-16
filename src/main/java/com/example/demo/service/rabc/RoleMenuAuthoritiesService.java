package com.example.demo.service.rabc;

import com.example.demo.dao.rabc.RoleMenuAuthoritiesMapper;
import com.example.demo.dao.rabc.RolesMapper;
import com.example.demo.model.entity.rabc.RoleMenuAuthorities;
import com.example.demo.model.entity.rabc.Roles;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.rabc.RoleMenuAuthoritiesVM;
import com.example.demo.model.viewModel.rabc.RolesVM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleMenuAuthoritiesService {
    private static Logger logger = LogManager.getLogger(RoleMenuAuthoritiesService.class);
    @Autowired
    RoleMenuAuthoritiesMapper roleMenuAuthoritiesMapper;

    public MessageResult<RoleMenuAuthorities> selectByPrimaryKey(int id) {
        MessageResult<RoleMenuAuthorities> messageResult = new MessageResult<>();
        try {
            RoleMenuAuthorities roleMenuAuthority = roleMenuAuthoritiesMapper.selectByPrimaryKey(id);
//          List<Users> list= new LinkedList<>();
//            list.add(users);
            messageResult.setData(roleMenuAuthority);
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> addRoleMenuAuthority(RoleMenuAuthorities roleMenuAuthority) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = roleMenuAuthoritiesMapper.insert(roleMenuAuthority);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> updateByPrimaryKey(RoleMenuAuthorities roleMenuAuthority) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = roleMenuAuthoritiesMapper.updateByPrimaryKey(roleMenuAuthority);
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
            Integer result = roleMenuAuthoritiesMapper.deleteByPrimaryKey(id);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<PageData<RoleMenuAuthoritiesVM>> getPageDataWithCount(RoleMenuAuthoritiesVM viewModel) {
        MessageResult<PageData<RoleMenuAuthoritiesVM>> message = new MessageResult<>();
        try {
            PageData<RoleMenuAuthoritiesVM> paegData = new PageData<>();
            Long count = roleMenuAuthoritiesMapper.getPageDataCount(viewModel);
            paegData.setCount(count);
            List<RoleMenuAuthoritiesVM> list = roleMenuAuthoritiesMapper.getPageData(viewModel);
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

    public MessageResult<List<RoleMenuAuthoritiesVM>> getPageData(RoleMenuAuthoritiesVM viewModel) {
        MessageResult<List<RoleMenuAuthoritiesVM>> message = new MessageResult<>();
        try {
            List<RoleMenuAuthoritiesVM> list = roleMenuAuthoritiesMapper.getPageData(viewModel);
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

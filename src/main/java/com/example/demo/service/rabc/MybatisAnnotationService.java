package com.example.demo.service.rabc;

import com.example.demo.dao.rabc.MybatisAnnotationMapper;
import com.example.demo.dao.rabc.UsersMapper;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.rabc.UsersVM;
import org.apache.ibatis.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MybatisAnnotationService {

    //int insert(Users user);


   // int batchInsert(List<Users> list);


  //  Users selectById(@Param("id") Integer id);


  //  int batchDelete(List<> list);


  //  List<Users> selectAll();
  private static Logger logger = LogManager.getLogger(MybatisAnnotationService.class);

    @Autowired
    MybatisAnnotationMapper mybatisAnnotationMapper;

    public MessageResult<Void> insert(Users user) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = mybatisAnnotationMapper.insert(user);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Void> batchInsert(List<Users> list) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = mybatisAnnotationMapper.batchInsert(list);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    public MessageResult<Users> selectById(Integer id) {
        MessageResult<Users> messageResult = new MessageResult<>();
        try {
            Users users = mybatisAnnotationMapper.selectById(id);
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

    public MessageResult<Void> batchDelete(List<Integer> list) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Integer result = mybatisAnnotationMapper.batchDelete(list);
            messageResult.setSuccess(result > 0);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }


        public MessageResult<List<Users>> selectAll() {
            MessageResult<List<Users>> message = new MessageResult<>();
            try {
                List<Users> list = mybatisAnnotationMapper.selectAll();
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

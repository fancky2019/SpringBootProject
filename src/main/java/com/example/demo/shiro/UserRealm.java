package com.example.demo.shiro;

import com.example.demo.dao.shiro.UserMapper;
import com.example.demo.model.entity.shiro.User;
import com.example.demo.service.shiro.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 授权
     * 这里不需要将用户的权限和角色放入到SimpleAuthorizationInfo中
     * 就算写了，也没用，因为动态代理是需要从数据库去查询的
     * 而这里仅仅只会在用户登录的时候执行一次，即在用户登录的时候就把权限和角色定死了
     * 即使在数据库修改了用户的权限和角色，只要用户没有退出登录，那么用户就还保有角色和权限
     * 直到用户重新登陆的时候才会从数据库拿到被修改后的角色和权限
     */

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //如果身份认证的时候没有传入User对象，这里只能取到userName
        //也就是SimpleAuthenticationInfo构造的时候第一个参数传递需要User对象
        String userName = (String) principalCollection.getPrimaryPrincipal();



//        // 查询用户角色，一个用户可能有多个角色
//        List<Role> roles = iRoleService.getUserRoles(user.getUserId());
//
//        for (Role role : roles) {
//            authorizationInfo.addRole(role.getRole());
//            // 根据角色查询权限
//            List<Permission> permissions = iPermissionService.getRolePermissions(role.getRoleId());
//            for (Permission p : permissions) {
//                authorizationInfo.addStringPermission(p.getPermission());
//            }
//        }
//        return authorizationInfo;
        return null;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //在token中获取用户名
        String username = (String) authenticationToken.getPrincipal();
        //从数据库中查数据
        User user = userService.selectByUserName(username);
        if (user == null) {
            throw new UnknownAccountException();
        }
//        User user = new User();
//        //从数据库中查数据

//        user.setUsername(username);
//        user.setPassword("123456");
//        //生成随机的字符串，即我们要用到的盐值
//        String salt = "salt";
//        //使用MD5加密
//        Md5Hash password = new Md5Hash(user.getPassword(), salt, 1024);
//        String saltPassword = String.valueOf(password);
//        //将加密后的密码和盐值放入到实体类中
//        user.setPassword(saltPassword);
//        user.setSalt(salt);


        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getUsername(), //用户名      //用户名
                user.getPassword(),                  //加密后的密码
                ByteSource.Util.bytes(user.getSalt()),  //随机盐
                getName()); //当前realm的名称





        //  写入session，但是移除password信息
//        user.setPassword("");
//        user.setSalt("");
//        //Session适用redis做缓存就不会保存内存
//        SecurityUtils.getSubject().getSession().setAttribute("UserInfo", user);
//        User sessionUser = (User) SecurityUtils.getSubject().getSession().getAttribute("UserInfo");
//


        return info;


    }
}


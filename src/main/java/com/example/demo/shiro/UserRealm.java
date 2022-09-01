//package com.example.demo.shiro;
//
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.authc.SimpleAuthenticationInfo;
//import org.apache.shiro.authc.UnknownAccountException;
//import org.apache.shiro.authz.AuthorizationInfo;
//import org.apache.shiro.realm.AuthorizingRealm;
//import org.apache.shiro.subject.PrincipalCollection;
//import org.apache.shiro.util.ByteSource;
//import org.apache.shiro.authc.*;
//
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class UserRealm extends AuthorizingRealm {
//
////    @Resource
////    private UserMapper userMapper;
//
//    /**
//     * 授权
//     * 这里不需要将用户的权限和角色放入到SimpleAuthorizationInfo中
//     * 就算写了，也没用，因为动态代理是需要从数据库去查询的
//     * 而这里仅仅只会在用户登录的时候执行一次，即在用户登录的时候就把权限和角色定死了
//     * 即使在数据库修改了用户的权限和角色，只要用户没有退出登录，那么用户就还保有角色和权限
//     * 直到用户重新登陆的时候才会从数据库拿到被修改后的角色和权限
//     */
//
//    @Override
//    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
//        return null;
//    }
//
//    //认证
//    @Override
//    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
//        //在token中获取用户名
//        String name = (String)authenticationToken.getPrincipal();
//        //从数据库查询用户信息
//        Map<String, Object> userMap = new HashMap<>();
//        userMap.put("name", name);
////        List<User> users =new ArrayList<>();// userMapper.selectByMap(userMap);
////
////        if (users.size() == 0) {
////            throw new UnknownAccountException("没有此账号");
////        }else{
////            User user = users.get(0);
////            return new SimpleAuthenticationInfo(name,       //用户名
////                    user.getPassword(),                     //加密后的密码
////                    ByteSource.Util.bytes(user.getSalt()),  //随机盐
////                    getName());                             //当前realm的名称
////        }
//        return new SimpleAuthenticationInfo(name,       //用户名
//                    "user password",                  //加密后的密码
//                    ByteSource.Util.bytes("sddsdssdsd"),  //随机盐
//                    getName());
//
//    }
//}
//

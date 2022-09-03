package com.example.demo.shiro;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class URLPathMatchingFilter extends PathMatchingFilter {
//    @Autowired
//    LoginService loginService;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
//
//        if (loginService==null){
//            loginService= SpringContextUtil.getContext().getBean(LoginService.class);
//        }
//        //请求的url
        String requestURL = getPathWithinApplication(request);
        System.out.println("请求的url :"+requestURL);
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()){
            // 如果没有登录, 直接返回true 进入登录流程
            return  false;
        }

//        String email = TokenManager.getEmail();
//        List<Upermission> permissions = loginService. upermissions(email);
//
        boolean hasPermission = false;
//        for (Upermission url : permissions) {
//
//            if (url.getUrl().equals(requestURL)){
//                hasPermission = true;
//                break;
//            }
//        }
        if (hasPermission){
            return true;
        }else {
            UnauthorizedException ex = new UnauthorizedException("当前用户没有访问路径" + requestURL + "的权限");
            subject.getSession().setAttribute("ex",ex);
            WebUtils.issueRedirect(request, response, "/unauthorized");
            return false;

        }

    }
}

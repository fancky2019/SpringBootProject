package com.example.demo.shiro;


import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


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
        boolean hasPermission = false;
        if (!subject.isAuthenticated()){


            // 如果没有登录, 直接返回true 进入登录流程
//            hasPermission=  false;

            //调试代码放行
            return true;
        }
        else {

            hasPermission=true;
        }

        //数据库查询用户的角色拥有的权限的url.判断请求的url 是否在数据库url中
//        String email = TokenManager.getEmail();
//        List<Upermission> permissions = loginService. upermissions(email);
//

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

            HttpServletRequest req = (HttpServletRequest)request;
            HttpServletResponse resp = (HttpServletResponse) response;
            UnauthorizedException ex = new UnauthorizedException("当前用户没有访问路径" + requestURL + "的权限");
            subject.getSession().setAttribute("ex",ex);
          //  WebUtils.issueRedirect(request, response, "/unauthorized");
            //前端Ajax请求，则不会重定向
            resp.setHeader("Access-Control-Allow-Origin",  req.getHeader("Origin"));
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.setContentType("application/json; charset=utf-8");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            JSONObject result = new JSONObject();
            result.put("message", "权限不足！");
            result.put("statusCode", -403);
            out.println(result);
            out.flush();
            out.close();
            return false;


        }

    }
}

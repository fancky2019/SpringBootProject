package com.example.demo.config;

import com.example.demo.shiro.ShiroRedisProperties;
import com.example.demo.shiro.URLPathMatchingFilter;
import com.example.demo.shiro.UserRealm;
import lombok.Data;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;


/*
1. Bean 用在方法上，Component用在类上， 用了这2个注解 就放入ioc了。注意：有@Bean的方法的类，必须加上@Configuration，表明这个是个配置类，相当于xml文件的作用。不然，你加上@Bean没有用啊！！

2.ConfigurationProperties 作用是绑定配置文件中的值和类的成员变量，使得new出的对象是有初始值的。但是，用了这个注解后，类并没有到容器中。必须要加@Component，放入容器。

3.如果不用Component，那么需要注入上面这个对象的类 必须加上@EnableConfigurationProperties。这样，使用了@EnableConfigurationProperties这个注解后，IOC容器中也会有了。那么你就可以在这个类中使用@AutoWired 等注解来注入上述组件。
 *




@ConfigurationProperties
@Component



@Configuration
@EnableConfigurationProperties(HelloServiceProperties.class)
@ConditionalOnClass(HelloService.class)
@ConditionalOnProperty(prefix = "hello", value = "enable", matchIfMissing = true)
 */
@EnableConfigurationProperties({ShiroRedisProperties.class})
@Configuration
public class ShiroConfiguration {

//    @Value("${spring.redis.host}")
//    private String host = "127.0.0.1";
//
//    @Value("${spring.redis.port}")
//
//    private int port = 6379;
//    @Value("${spring.redis.timeout}")
//    private int timeout;
//
//    @Value("${spring.redis.password}")
//    private String password = "fancky123456";

    /*
      如果bean    @Autowired 无法注入，就采用在方法参数中注入

        @Bean
      public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {

       */
    //
    @Autowired
    private  ShiroRedisProperties shiroProperties;



//    //LifecycleBeanPostProcessor 会造成autowire bean  无法注入，不知道原因
//    //从shiroConfiguration 中分离
//    @Bean
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
//        return new LifecycleBeanPostProcessor();
//    }


    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，因为在
     * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
     * <p>
     * Filter Chain定义说明
     * 1、一个URL可以配置多个Filter，使用逗号分隔
     * 2、当设置多个过滤器时，全部验证通过，才视为通过
     * 3、部分过滤器可指定参数，如perms，roles
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        System.out.println("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //前后端分离不需要设置
//        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
//        shiroFilterFactoryBean.setLoginUrl("/login");
//        // 登录成功后要跳转的链接(没用,在js中跳转了)
//        shiroFilterFactoryBean.setSuccessUrl("/index");
//        //未授权界面
//        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        //自定义拦截器
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        //限制同一帐号同时在线的个数。
//        filtersMap.put("kickout", kickoutSessionControlFilter());
        //访问权限配置
        filtersMap.put("requestURL", getURLPathMatchingFilter());

        shiroFilterFactoryBean.setFilters(filtersMap);

        //拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();


        /* 配置映射关系*/
        //authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问
        filterChainDefinitionMap.put("/shiro/login", "anon");
        filterChainDefinitionMap.put("/shiro/register", "anon");
        filterChainDefinitionMap.put("/index", "authc");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");

        /*加入自定义过滤器*/
//        filterChainDefinitionMap.put("/**", "kickout");
        filterChainDefinitionMap.put("/sbp/utility/*", "anon");
        //不需要授权的写在前面
        //所有的路径都通过过滤器
        filterChainDefinitionMap.put("/**", "requestURL");


        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;

    }

    /**
     * 访问 权限 拦截器
     *
     * @return
     */
    public URLPathMatchingFilter getURLPathMatchingFilter() {
        return new URLPathMatchingFilter();
    }

    /**
     * 自定义域
     *
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm.
        securityManager.setRealm(getRealm());

        // 自定义缓存实现 使用redis
//        securityManager.setCacheManager(cacheManager());
//        securityManager.setSessionManager(sessionManager());
//        注入记住我管理器;
//        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    //创建自定义Realm
    @Bean
    public Realm getRealm() {
        //给自定义的realm设置密码匹配器
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(md5HashedCredentialsMatcher());
        return userRealm;
    }


    //密码匹配器
    @Bean
    public HashedCredentialsMatcher md5HashedCredentialsMatcher() {
        //获取hash凭证匹配器
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //设置hash凭证匹配器使用的算法（MD5）
        hashedCredentialsMatcher.setHashAlgorithmName("md5");

        //适用盐，加盐（加密）次数
        //默认就是1
        //如果使用散列（加密次数），则需要设置散列的次数：1024次，与注册时的加密次数是一致的
//        hashedCredentialsMatcher.setHashIterations(1);
        // 默认 true 密码加密用hex编码; false 用base64编码
//        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }

    /**
     * 如果不写这个配置，那么在启动程序的时候，会出现jdk动态代理与CGLIB代理混乱的问题
     * 而这个配置的意思，就是指定代理为CGLIB代理
     * 至于这两种代理，可以去百度，这里就不进行赘述了
     * 这是shiro和spring boot整合所产生的问题
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }


//    /**
//     * 开启shiro aop注解支持.
//     * 使用代理方式;所以需要开启代码支持;
//     *
//     * @param securityManager
//     * @return
//     */
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
//        return authorizationAttributeSourceAdvisor;
//    }

    /**
     * cacheManager 缓存 redis实现
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        // 必须要设置主键名称，shiro-redis 插件用过这个缓存用户信息
        redisCacheManager.setPrincipalIdFieldName("userId");
        return redisCacheManager;
    }

    /**
     * Session Manager
     * 使用的是shiro-redis开源插件
     */
    @Bean(name = "sessionManager")
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        return sessionManager;
    }

    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * 使用的是shiro-redis开源插件
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();

        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    /**
     * 配置shiro redisManager
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean
    public RedisManager redisManager() {

        RedisManager redisManager = new RedisManager();
        redisManager.setHost(shiroProperties.getHost() + ":" + shiroProperties.getPort());
//        redisManager.setPort(port);
//        redisManager.setExpire(1800);// 配置缓存过期时间
//        redisManager.setTimeout(timeout);
        redisManager.setPassword(shiroProperties.getPassword());



        return redisManager;
    }


//
//    /**
//     * cookie管理对象;记住我功能
//     *
//     * @return
//     */
//    public CookieRememberMeManager rememberMeManager() {
//        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//        cookieRememberMeManager.setCookie(rememberMeCookie());
//        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
//        cookieRememberMeManager.setCipherKey(Base64.decode("3AvVhmFLUs0KTA3Kprsdag=="));
//        return cookieRememberMeManager;
//    }
//
//    /**
//     * cookie对象;
//     *
//     * @return
//     */
//    public SimpleCookie rememberMeCookie() {
//        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
//        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
//        simpleCookie.setMaxAge(2592000);
//        return simpleCookie;
//    }
//


//    /**
//     * 限制同一账号登录同时登录人数控制
//     *
//     * @return
//     */
//    public KickoutSessionControlFilter kickoutSessionControlFilter() {
//        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
//        //使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
//        //这里我们还是用之前shiro使用的redisManager()实现的cacheManager()缓存管理
//        //也可以重新另写一个，重新配置缓存时间之类的自定义缓存属性
//        kickoutSessionControlFilter.setCacheManager(cacheManager());
//        //用于根据会话ID，获取会话进行踢出操作的；
//        kickoutSessionControlFilter.setSessionManager(sessionManager());
//        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；踢出顺序。
//        kickoutSessionControlFilter.setKickoutAfter(false);
//        //同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
//        kickoutSessionControlFilter.setMaxSession(1);
//        //被踢出后重定向到的地址；
//        kickoutSessionControlFilter.setKickoutUrl("kickout");
//        return kickoutSessionControlFilter;
//    }

}

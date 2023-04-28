package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/17 17:37
 */
//web过滤器，拦截所有请求
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，可以使识别通配符
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestURI = httpServletRequest.getRequestURI();
        log.info("============拦截到请求：{}", requestURI);

        //定义不需要处理的请求路径
        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/upload",
//                "/common/download"
                "/user/sendMsg",    //移动端发送短信
                "/user/login"       //移动端登录
        };

        //check = true 不需要处理
        if (check(uris, requestURI)) {
            log.info("============本次请求{}不需要处理：", requestURI);
            chain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        //【判断后台管理端】，需要拦截的uri，判断是否登入，登入则放行。没有则返回未登入
        //TODO 2023年1月24日17:47:06 同一个session下，user 移动端登入之后管理台刷新也是登入。需要加url判断
        if(httpServletRequest.getSession().getAttribute("employee")!=null){
            log.info("============用户已经登入，用户id为：{}：", httpServletRequest.getSession().getAttribute("employee"));

            //获得session中当前登入用户id，放入threadlocal对象
            // （threadlocal是thread的一个变量，每个thread有副本相互隔离）中
            log.info("loginfilter========== 当前线程id：{}, name: {}", Thread.currentThread().getId(), Thread.currentThread().getName());
            BaseContext.setValue((Long)httpServletRequest.getSession().getAttribute("employee"));

            chain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        //【判断移动端】，需要拦截的uri，判断是否登入，登入则放行。没有则返回未登入
        if(httpServletRequest.getSession().getAttribute("user")!=null){
            log.info("============用户已经登入，用户id为：{}：", httpServletRequest.getSession().getAttribute("user"));

            //获得session中当前登入用户id，放入threadlocal对象
            // （threadlocal是thread的一个变量，每个thread有副本相互隔离）中
            log.info("loginfilter========== 当前线程id：{}, name: {}", Thread.currentThread().getId(), Thread.currentThread().getName());
            BaseContext.setValue((Long)httpServletRequest.getSession().getAttribute("user"));

            chain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        //没有登入
        log.info("============NOTLOGIN");
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    public boolean check(String[] uris, String requestURI) {
        for (String s : uris) {
            boolean match = ANT_PATH_MATCHER.match(s, requestURI);
            //有一个url匹配上就是不需要处理的请求
            if (match) {
                return true;
            }
        }
        return false;
    }
}

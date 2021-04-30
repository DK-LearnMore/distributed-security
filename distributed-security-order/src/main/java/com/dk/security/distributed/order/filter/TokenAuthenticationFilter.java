package com.dk.security.distributed.order.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dk.security.distributed.order.entity.User;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 1.从header中获得传递过来的解析后的token，因为传过来的时候没有加密，这里就不需要解密了，此时获取的时json格式
        String token = httpServletRequest.getHeader("json-token");
        // 转换成json对象
        JSONObject jsonObject = JSON.parseObject(token);
        // 取出用户信息和权限，之前存储为什么类型就怎么取
        //String principal = jsonObject.getString("principal"); // 用户信息

        // 取出数据封装到User中
        User principal = JSON.parseObject(jsonObject.getString("principal"), User.class);

        JSONArray authoritiesArray = jsonObject.getJSONArray("authorities"); // 用户权限
        String[] authorities = authoritiesArray.toArray(new String[authoritiesArray.size()]); // 转成数组
        // 2.将用户信息和权限填充到UsernamePasswordAuthenticationToken对象中，之前使用的时UsernamePasswordAuthenticationToken，所以这边也使用这个进行填充
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList(authorities));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        // 3.将authentication对象填充到springsecurity的上下文中
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 继续执行下一个过滤器
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }


}

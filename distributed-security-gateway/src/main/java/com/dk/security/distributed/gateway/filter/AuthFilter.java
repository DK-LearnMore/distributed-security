package com.dk.security.distributed.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthFilter extends ZuulFilter {
    // 是否过滤
    @Override
    public boolean shouldFilter() {
        return true;
    }
    // pre,请求之前进行拦截
    @Override
    public String filterType() {
        return "pre";
    }

    // 0，数字越小拦截的优先级越优先
    @Override
    public int filterOrder() {
        return 0;
    }
    // 过滤操作，解析token转换成明文存储为json传递给微服务（解析jwt可以看之前jwt笔记）
    @Override
    public Object run() throws ZuulException {
        // 解析令牌
        // 获取上下文信息,为了之后转发给微服务时加上对象头
        RequestContext ctx = RequestContext.getCurrentContext();
        // 从安全上下文获取authentication对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 如果authentication对象不是auth2类型，不进行解析直接返回null不允许访问
        if (!(authentication instanceof OAuth2Authentication)){
            return null;
        }
        // 如果是oauth2类型，转换成oauth2，并取出用户对象信息
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)authentication;
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        // 1.获取当前用户身份信息
        String principal = userAuthentication.getName();
        // 2.获取当前用户权限信息
        List<String> authorities = new ArrayList<>();
        // 从userAuthentication对象中取出权限，放在authorities中
        userAuthentication.getAuthorities().stream().forEach(c->authorities.add(((GrantedAuthority)c).getAuthority()));
        // 存储身份信息和权限信息
        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
        Map<String,String> requestParameters = oAuth2Request.getRequestParameters();
        Map<String,Object> jsonToken = new HashMap<>(requestParameters);
        if(userAuthentication != null){
            jsonToken.put("principal",principal);
            jsonToken.put("authorities",authorities);
        }
        // 3.信息转化为在json中,这边就不进行加密了，可以使用加密的工具类（如EncryptUtil）进行加密，后面在order服务器中获取时进行系欸，加入http的header中
        // 4.转发给微服务
        ctx.addZuulRequestHeader("json-token", JSON.toJSONString(jsonToken));
        return null;
    }
}

package com.dk.security.distributed.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
public class ResourceServerConfig{
    public static final String RESOURCE_ID = "res1";// 资源名称,与我们之前的客户端配置相对应

    // uaa资源服务配置
    @Configuration
    @EnableResourceServer
    public class UAAServerConfig extends ResourceServerConfigurerAdapter{
        @Autowired
        private TokenStore tokenStore;
        // 配置信息
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
           resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
        }

        // 访问策略
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/uaa/**").permitAll();// /uaa/**的所有资源都放行
        }
    }

    // order资源服务配置
    @Configuration
    @EnableResourceServer
    public class OrderServerConfig extends ResourceServerConfigurerAdapter{
        @Autowired
        private TokenStore tokenStore;
        // 配置信息
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(true);
        }

        // 访问策略
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/order/**").access("#oauth2.hasScope('ROLE_API')");// 拥有这个权限才能放行（令牌解析后就可以得到权限信息）
        }
    }
}














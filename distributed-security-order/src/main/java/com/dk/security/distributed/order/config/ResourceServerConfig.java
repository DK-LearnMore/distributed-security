package com.dk.security.distributed.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    public static final String RESOURCE_ID = "res1";// 资源名称,与我们之前的客户端配置相对应
    @Autowired
    private TokenStore tokenStore;
    // 资源服务的配置信息
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID) // 资源id
                //.tokenServices(tokenServices()) // 验证令牌的服务,怎么验证令牌
                .tokenStore(tokenStore)
                .stateless(true);
    }

    // 资源访问的策略
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/**").access("#oauth2.hasScope('ROLE_ADMIN')") //和之前客户端配置对应
                .and().csrf().disable() // 关闭csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // STATELESS不创建session,也不访问session
    }

    // 资源服务令牌解析服务
//    @Bean
//    public ResourceServerTokenServices tokenServices(){
//        // 使用远程服务请求授权服务器校验token,必须指定校验token的url,client_id,client_secret;和我们之前的客户端配置和安全约束对应
//        RemoteTokenServices services = new RemoteTokenServices();
//        services.setCheckTokenEndpointUrl("http://localhost:53020/uaa/oauth/check_token"); // 在安全约束配置中开放了//oauth/check_token
//        services.setClientId("c1");
//        services.setClientSecret("secret");
//        return services;
//    }
}














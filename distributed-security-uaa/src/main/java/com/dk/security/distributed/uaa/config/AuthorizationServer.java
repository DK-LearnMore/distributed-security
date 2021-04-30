package com.dk.security.distributed.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    // 客户端的详情服务
    // clients表示客户端信息
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.inMemory() // 使用in-memory存储
//                .withClient("c1") //client_id
//                .secret(new BCryptPasswordEncoder().encode("secret")) // 客户端密钥,采用BCrypt
//                .resourceIds("res1") // 可访问资源的资源列表
//                .authorizedGrantTypes("authorization_code","password","client_credentials","implicit","refresh_token") // 该client允许的授权类型
//                .scopes("all") // 允许的授权范围
//                .autoApprove(false) // false跳转到授权页面
//                .redirectUris("http://www.baidu.com"); // 客户端的回调地址
        //使用从数据库中读取数据配置我们的客户端
        clients.withClientDetails(clientDetailsService);
    }

    //令牌管理服务
    @Bean
    public AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices services = new DefaultTokenServices();
        services.setClientDetailsService(clientDetailsService); // 客户端详情服务
        services.setSupportRefreshToken(true); // 支持刷新令牌
        services.setTokenStore(tokenStore); // 令牌存储策略

        // 令牌增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter));
        services.setTokenEnhancer(tokenEnhancerChain);

        services.setAccessTokenValiditySeconds(7200); // 令牌默认有效期2消失
        services.setRefreshTokenValiditySeconds(259200); // 刷新令牌默认有效期三天
        return services;
    }

    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;
    // 认证管理器
    @Autowired
    private AuthenticationManager authenticationManager;
    // 设置授权码模式的授权码如何获取
    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource){
//        return new InMemoryAuthorizationCodeServices();//采用内存方式
        return new JdbcAuthorizationCodeServices(dataSource);//从数据库中读取
    }

    // 令牌访问端点
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager) // 认证管理器
                .authorizationCodeServices(authorizationCodeServices) // 授权码服务
                .tokenServices(tokenServices()) //令牌管理服务
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }
    // 安全约束
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()") //oauth/token_key是公开的
                .checkTokenAccess("permitAll()") //oauth/check_token公开,允许所有人访问
                .allowFormAuthenticationForClients(); //表单认证(申请令牌)
    }
    // 自定义客户端配置
    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource){ // DataSource为我们yml文件配置的数据源，spring帮我们注入
        ClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        ((JdbcClientDetailsService)clientDetailsService).setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }










}

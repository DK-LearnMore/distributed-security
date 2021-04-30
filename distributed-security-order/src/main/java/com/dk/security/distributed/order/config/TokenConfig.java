package com.dk.security.distributed.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class TokenConfig {
    // 密钥
    private static final String SINGLE_KEY = "uaa123";
    @Bean
    public TokenStore tokenStore(){
        // 使用JWT存储令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }
    // 产生令牌的方式
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SINGLE_KEY); // 对称密钥,资源服务器使用该密钥来验证
        return converter;
    }
}

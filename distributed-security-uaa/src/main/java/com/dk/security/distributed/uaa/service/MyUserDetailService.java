package com.dk.security.distributed.uaa.service;

import com.alibaba.fastjson.JSON;
import com.dk.security.distributed.uaa.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserDao userDao;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询到用户
        com.dk.security.distributed.uaa.entity.User user = userDao.selectUser(username);
        if (user == null){
            return null;// 返回的对象交给Provider去处理，返回的不是userDetails，Provider就会抛异常
        }
        // 根据用户id查询到对应的权限
        String[] permission = userDao.selectPermission(user.getId());
        // 将用户的信息给userDetails对象
        // 因为我们的密码编码器使用的是BCrypt,所以需要我们数据库中的密码必须符合他的编码规范，通过BCrypt.hashpw("dk",BCrypt.gensalt())将我们的密码加密,第二个参数是随机盐

        // 扩展用户信息，存入json数据内容作为username的内容
        UserDetails userDetails = User.withUsername(JSON.toJSONString(user)).password(user.getPassword()).authorities(permission).build();

        return userDetails;
    }
}

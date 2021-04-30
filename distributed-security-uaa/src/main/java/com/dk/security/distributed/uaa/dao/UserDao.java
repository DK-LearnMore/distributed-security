package com.dk.security.distributed.uaa.dao;

import com.dk.security.distributed.uaa.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper //也可以在主启动类加@MapperScan
public interface UserDao {
    //通过用户名查询到用户信息
    User selectUser(String username);
    //通过用户id查询到权限,获取到code也就是权限的数组对象，为什么是数组，因为userDetails中传入的参数为数组对象
    String[] selectPermission(BigDecimal userId);
}

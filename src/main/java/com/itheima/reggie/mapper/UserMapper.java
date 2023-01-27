package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/24 7:28
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

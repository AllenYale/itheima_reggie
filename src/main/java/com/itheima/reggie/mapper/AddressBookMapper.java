package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/24 8:51
 */
//使用mybatisplus框架，mapper直接继承BaseMapper<pojo>类，再加注解@Mapper
// 就可直接调用单标curd方法
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}

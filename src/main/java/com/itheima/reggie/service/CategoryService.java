package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 14:25
 */
public interface CategoryService extends IService<Category> {

    /**
     * 根据id删除分类，删除之前需要判断
     * @param id
     */
    public void remove(long id);
}

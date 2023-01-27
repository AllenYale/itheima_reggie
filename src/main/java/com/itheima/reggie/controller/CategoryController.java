package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 14:29
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类/菜单信息
     * @param category
     * @return
     */
    @PostMapping
    public R<String> saveCategory(@RequestBody Category category){
        log.info("新增category分类/菜单信息：{}", category);
        categoryService.save(category);
        return R.success("add "+(category.getType()==1?"cate":"menu")+" succeed!!");
    }

    /**
     * 分页查询category
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize){
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("delete category which id is :{}", ids);

        //删除前需要逻辑判断，如果cate有关联菜品或者套餐则不能删除
        categoryService.remove(ids);//按住Ctrl + Alt，再鼠标左键点击该方法，就能去到实现类
//        categoryService.removeById(id);
        return R.success("del success...");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("update category....., {}", category.toString());
        categoryService.updateById(category);
        return R.success("update succeed...");
    }

    //查询分类list or 按照条件（categoryID）查询分类list
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        log.info("加载分类列表：R<List<Category>> list(Category category):{}", category);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort);

        List<Category> categoryList = categoryService.list(lambdaQueryWrapper);
        return R.success(categoryList);
    }

}

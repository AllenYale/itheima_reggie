package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/17 15:32
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登入
     * @param employee
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest httpServletRequest){
        //1. pwd密码md5加密处理
        String password = employee.getPassword();
        /*DigestUtils 是 Apache Commons Codec 中提供的一个工具类，用于实现各种哈希算法和消息摘要算法，包括 MD5、SHA-1、SHA-256 等常见的算法。它是通过对 Java 标准库中的 MessageDigest 类进行封装和简化，提供了更加易用和方便的接口。*/
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2. username查询db
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //TODO: 2023年4月28日00:03:17 还是没理解方法引用，重新看一遍新特性。
        // ？？？2023年1月17日16:21:35方法引用 还是没理解方法引用，重新看一遍新特性。
        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        //username 在数据库中，已经设计成unique index唯一索引
        Employee emp = employeeService.getOne(lambdaQueryWrapper);

        //3. 如果没有查询到返回登入失败
        if(emp == null){
            return R.error("登入失败");
        }

        //4. 密码对比，如果不一致返回登入失败
        if(!emp.getPassword().equals(password)){
            return R.error("登入失败");
        }

        //5. 查看emp status
        if(emp.getStatus() == 0){
            return R.error("status is invaild");
        }

        //6. 登入成功，将员工id放入session
        httpServletRequest.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);

    }

    /**
     * 退出登入
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest){
        httpServletRequest.removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request){
        log.info("新增员工，员工信息{} ", employee.toString());

        //init密码123456 md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long id = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(id);
//        employee.setCreateUser(id);

        employeeService.save(employee);
        log.info("save controller========== 当前线程id：{}, name: {}", Thread.currentThread().getId(), Thread.currentThread().getName());

        return R.success("success emp save");
    }

    /**
     * 员工信息分页查询，使用MP分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        //分页构造器
        Page pageInfo = new Page(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotBlank(name), Employee::getUsername, name);
        lambdaQueryWrapper.orderByDesc(Employee::getCreateTime);

        employeeService.pageMaps(pageInfo, lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 更新员工
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updateEmp(@RequestBody Employee employee, HttpServletRequest request){
        log.info("更新操作，empID：{}",employee.toString());

        //填充字段，完善数据库更新
        Long id = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(id);
        employee.setUpdateTime(LocalDateTime.now());

        boolean b = employeeService.updateById(employee);

        return b ? R.success("update success!!"):R.error("update failed!");

    }

    /**
     * 根据id查询emp信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable Long id){
        log.info("通过id查询employee，id：{}", id);

        Employee employeeById = employeeService.getById(id);
        return R.success(employeeById);

    }









}

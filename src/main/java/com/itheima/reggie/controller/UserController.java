package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.VerificationCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/22 21:07
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userSerive;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();
        if(StringUtils.isNotBlank(phone)){
            //生成随机4位验证码
            String code = VerificationCodeUtils.generateValidateCode(4).toString();
            log.info("code:::{}",code);
            //有效期
            String validMins = "5";
            //调用腾讯云api发送验证码
//            SMSUtils_Tencent.sendMessage("+86"+phone, code, validMins);

            //放入session  phone=code
//            session.setAttribute(phone, code);

            //优化：放入redis中，并设置有效期
            redisTemplate.opsForValue().set(phone, code, Long.valueOf(validMins), TimeUnit.MINUTES);

            return R.success("发送验证码成功");

        }
        return R.success("发送验证码失败");
    }

    /**
     * 移动端用户登入
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession httpSession){//RequestBody Map 类型参数可以接受前端零散json数据（不必用dto封装）
        //接收移动端传递参数：手机号+验证码
        log.info("map, {}", map);

        //校验验证码是否正确，验证码和session中手机号存着的code是否一致
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");

//        String codeByPhone =  httpSession.getAttribute(phone);
        //优化：从redis中取生成的验证码code
        String codeByPhone = (String) redisTemplate.opsForValue().get(phone);

        log.info("login phone code codeByphone:{}, {}, {}",phone, code, codeByPhone);
        if(code.equals(codeByPhone)){
        //一致，如果是新手机就帮他注册（表中添加记录），老账号就直接登入
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userSerive.getOne(lambdaQueryWrapper);
            if(user==null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userSerive.save(user);
            }
            //登入成功，将user信息存入session
            httpSession.setAttribute("user",user.getId());
            //优化：删除redis中缓存验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        //不一致 返回验证码失败
        return R.error("code error..login fail");
    }
}

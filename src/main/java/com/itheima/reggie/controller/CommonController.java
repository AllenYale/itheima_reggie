package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 17:13
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value(value = "${reggie.file.basepath}")
    private String fileBasePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file/*spring框架封装的类，在spring-web jar包中，参数名和前端上传文件name必须一致*/) {
        log.info("Common Controller， multipart file 文件上传中：：：{}", file.toString());

        //file API获得 获得原始文件名
        String originalFilename = file.getOriginalFilename();
        String originalFileNamePrefix = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        //获取后缀比如 .jpg .png
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        //生成随机30多位uuid：5aece8fb-54a9-484e-9389-44b35d869c1e
        UUID randomUUID = UUID.randomUUID();
        String newFileName = randomUUID + originalFileNamePrefix + suffix;

        //判断basePath是否存在。不存在就创建一个目录用于转发文件transferTo
        File dir = new File(fileBasePath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            //调用Spring框架API转存文件： MultipartFile transferTo方法，将文件转存到某个地方File dest
            file.transferTo(new File(fileBasePath +  newFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //返回页面文件名，新增菜品需要把文件名保存到表中。（文件路径在项目配置文件配置：file\basePath）；也可以直接在DB中存文件地址，读取时直接读相应地址文件
        return R.success(newFileName);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param httpServletResponse
     */
    @GetMapping("/download")
    public void download(String name/*文件名称*/, HttpServletResponse httpServletResponse/*获得输出流，二进制响应  */) {

        try (FileInputStream fileInputStream = new FileInputStream(fileBasePath + name);
             ServletOutputStream outputStream = httpServletResponse.getOutputStream();) {

            //设置响应回去的文件类型
            httpServletResponse.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

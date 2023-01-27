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
    public R<String> upload(MultipartFile file) {
        log.info("Commoncontroller， multipartfile 文件上传中：：：{}", file.toString());

//        String name = file.getName(); //形参名字
        //获得原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        UUID randomUUID = UUID.randomUUID();
        String newFileName = randomUUID + suffix;

        File dir = new File(fileBasePath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            file.transferTo(new File(fileBasePath + newFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(newFileName);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param httpServletResponse
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse httpServletResponse) {

        try (FileInputStream fileInputStream = new FileInputStream(fileBasePath + name);
             ServletOutputStream outputStream = httpServletResponse.getOutputStream();) {

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

package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

    @Resource
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     */
    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传, file: {}", file);
        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();
            // 文件后缀
            if (originalFilename != null) {
                String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                // 构造新文件名
                String newFilename = UUID.randomUUID().toString() + suffix;
                // 上传文件
                String url = aliOssUtil.upload(file.getBytes(), newFilename);
                return Result.success(url);
            }
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException(e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }


}

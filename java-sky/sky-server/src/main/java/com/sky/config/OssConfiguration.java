package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Oss配置类，用于创建AliOssUtil实例
 * @author 上玄
 * @since 2025-03-16
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("初始化阿里云OSS配置{}", aliOssProperties);
        return AliOssUtil.builder()
               .endpoint(aliOssProperties.getEndpoint())
               .accessKeyId(aliOssProperties.getAccessKeyId())
               .accessKeySecret(aliOssProperties.getAccessKeySecret())
               .bucketName(aliOssProperties.getBucketName())
               .build();

    }
}

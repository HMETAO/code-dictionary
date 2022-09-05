package com.hmetao.code_dictionary.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.hmetao.code_dictionary.properties.AliOSSProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.HashMap;

@Slf4j
public class AliOssUtils {


    public static void upload(HashMap<String, InputStream> uploadFileMap, AliOSSProperties aliOSSProperties) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(aliOSSProperties.getEndpoint(),
                aliOSSProperties.getAccessKeyId(),
                aliOSSProperties.getAccessKeySecret());
        try {
            // 创建PutObject请求。
            uploadFileMap.forEach((fileName, inputStream) -> {
                log.info("AliOssUtils === > 开始上传文件至OSS：" + fileName);
                ossClient.putObject(aliOSSProperties.getBucketName(), fileName, inputStream);
            });
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


}

package com.hmetao.code_dictionary.utils;

import com.google.gson.Gson;
import com.hmetao.code_dictionary.properties.QiNiuProperties;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QiniuUtil {
    public static void upload2qiniu(QiNiuProperties qiuProperties, byte[] bytes, String fileName) throws RuntimeException {
        //构造一个带指定 Region 对象的配置类，指定存储区域，和存储空间选择的区域一致
        Configuration cfg = new Configuration(Region.huanan());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String accessKey = qiuProperties.getAK();
        String secretKey = qiuProperties.getSK();
        String bucket = qiuProperties.getBT();
        try {
            //认证
            Auth auth = Auth.create(accessKey, secretKey);
            //认证通过后得到token（令牌）
            String upToken = auth.uploadToken(bucket);
            try {
                //上传文件,参数：字节数组，key，token令牌
                //key: 建议我们自已生成一个不重复的名称
                Response response = uploadManager.put(bytes, fileName, upToken);
                //解析上传成功的结果
                new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                log.info("QiniuUtils === > 文件 {} 上传成功", fileName);
            } catch (QiniuException ex) {
                Response r = ex.response;
                log.error("QiniuUtils === > " + r.bodyString(), ex);
                throw new RuntimeException(r.bodyString());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static void deleteQiniu(QiNiuProperties qiuProperties, String fileName) {
        String accessKey = qiuProperties.getAK();
        String secretKey = qiuProperties.getSK();
        String bucket = qiuProperties.getBT();
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());

        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, fileName);
            log.info("QiniuUtils === > 文件 {} 删除成功", fileName);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            log.error("QiniuUtils === > " + ex.response.toString(), ex);
        }
    }
}

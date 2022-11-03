package com.ljj.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Testtest {

    @Resource
    OSSClient ossClient;

    @Test
    public void test()throws Exception{
        String bucketName = "gulimall9605";
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = "E:\\TEST\\test1.jpg";


        InputStream inputStream = new FileInputStream(objectName);

        ossClient.putObject(bucketName,"test1",inputStream);
        System.out.println("上传完成");
    }
}

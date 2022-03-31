package com.zhongzhou;

import com.zhongzhou.api.service.impl.WxLoginServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ExampleApplicationTests {
    @Resource
    WxLoginServiceImpl wxLoginService;

    @Test
    void contextLoads() {
    }

}

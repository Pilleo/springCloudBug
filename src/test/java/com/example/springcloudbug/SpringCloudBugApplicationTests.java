package com.example.springcloudbug;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.function.adapter.aws.FunctionInvoker;

class SpringCloudBugApplicationTests {

    @Test
    void contextLoads() {
        System.setProperty("MAIN_CLASS", SpringCloudBugApplication.class.getCanonicalName());
        FunctionInvoker functionInvoker = new FunctionInvoker();
        System.clearProperty("MAIN_CLASS");
    }

}

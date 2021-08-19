package com.example.springcloudbug;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.function.adapter.aws.FunctionInvoker;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpringCloudBugApplicationTests {

    private static byte[] kinesisJson;
    private static byte[] sqsJson;

    private static FunctionInvoker functionInvoker;

    @BeforeAll
    static void init() throws Exception {
        System.setProperty("MAIN_CLASS", SpringCloudBugApplication.class.getCanonicalName());
        functionInvoker = new FunctionInvoker();
        System.clearProperty("MAIN_CLASS");


        kinesisJson =
                FileCopyUtils.copyToByteArray(
                        SpringCloudBugApplicationTests.class.getResourceAsStream(
                                "/validKinesis.json"));
        sqsJson =
                FileCopyUtils.copyToByteArray(
                        SpringCloudBugApplicationTests.class.getResourceAsStream(
                                "/validSqs.json"));

    }


    @Test
    void sqsRequestProcessedSuccessfully() throws IOException {
        ByteArrayOutputStream outputSqs = new ByteArrayOutputStream();
        functionInvoker.handleRequest(new ByteArrayInputStream(sqsJson), outputSqs, null);
        assertEquals(SpringCloudBugApplication.SQS_FUN, outputSqs.toString(UTF_8));
    }

    @Test
    void kinesisrequestsProcessedSuccessfully() throws IOException {
        ByteArrayOutputStream outputKinesis = new ByteArrayOutputStream();
        functionInvoker.handleRequest(new ByteArrayInputStream(kinesisJson), outputKinesis, null);
        assertEquals("\""+SpringCloudBugApplication.KINESIS_FUN+"\"", outputKinesis.toString(UTF_8));
    }


}

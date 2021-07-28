package com.example.springcloudbug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.Function;

@SpringBootConfiguration
public class SpringCloudBugApplication implements ApplicationContextInitializer<GenericApplicationContext> {

    public static void main(String[] args) {
        FunctionalSpringApplication.run(SpringCloudBugApplication.class, args);
    }

    @Override
    public void initialize(GenericApplicationContext c) {
        c.registerBean("bugFunction", FunctionRegistration.class, () -> function(c));
    }

    private FunctionRegistration<Function<String, String>> function(GenericApplicationContext c) {
        Function<String, String> function = (str) -> str + str;
        return new FunctionRegistration<Function<String, String>>(function)
                .type(FunctionType.from(String.class).to(String.class).getType());
    }
}

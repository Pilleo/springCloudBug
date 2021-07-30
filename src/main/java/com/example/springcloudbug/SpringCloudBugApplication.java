package com.example.springcloudbug;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.Function;

@SpringBootConfiguration
public class SpringCloudBugApplication implements ApplicationContextInitializer<GenericApplicationContext> {
    public static final String FUNCTION = "fucntion";

    public static void main(String[] args) {
        FunctionalSpringApplication.run(SpringCloudBugApplication.class, args);
    }

    @Override
    public void initialize(GenericApplicationContext c) {
        c.registerBean("bugFunctionRegistration", FunctionRegistration.class, () -> function(c));
        Function<String, String> function = (str) -> str + str;

        c.registerBean(FUNCTION,Function.class,()-> function);
    }

    private FunctionRegistration<Function<String, String>> function(GenericApplicationContext c) {
        return new FunctionRegistration<Function<String, String>>(c.getBean(FUNCTION, Function.class))
                .type(FunctionType.from(String.class).to(String.class).getType());
    }
}

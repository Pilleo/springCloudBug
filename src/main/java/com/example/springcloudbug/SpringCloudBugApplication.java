package com.example.springcloudbug;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.MessageRoutingCallback;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.messaging.Message;

import java.util.function.Function;

@SpringBootApplication
public class SpringCloudBugApplication implements ApplicationContextInitializer<GenericApplicationContext> {

    public static final String SQS_FUN = "sqs";
    public static final String KINESIS_FUN = "kinesis";

    /*
     * You need this main method (empty) or explicit <start-class>example.FunctionConfiguration</start-class>
     * in the POM to ensure boot plug-in makes the correct entry
     */
    public static void main(String[] args) {
        // empty unless using Custom runtime at which point it should include
        // FunctionalSpringApplication.run(FunctionConfiguration.class, args);
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean(MessageRoutingCallback.class, () -> new RoutingCallback(context.getBean(ObjectMapper.class)));

        context.registerBean(SQS_FUN, FunctionRegistration.class,
                () -> new FunctionRegistration<>(sqsEventStringFunction()).type(
                        FunctionType.from(SQSEvent.class).to(String.class)));

        context.registerBean(KINESIS_FUN, FunctionRegistration.class,
                () -> new FunctionRegistration<>(kinesisEventStringFunction()).type(
                        FunctionType.from(KinesisEvent.class).to(String.class)));

    }

    private Function<SQSEvent, String> sqsEventStringFunction() {
        return (sqsEvent) -> SQS_FUN;
    }

    private Function<KinesisEvent, String> kinesisEventStringFunction() {
        return (KinesisEvent kinesisEvent) -> KINESIS_FUN;
    }

    public static class RoutingCallback implements MessageRoutingCallback {
        private final ObjectMapper objectMapper;

        public RoutingCallback(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public String functionDefinition(Message<?> messageWithAmountEvent) {

            try {
                byte[] payload = (byte[]) messageWithAmountEvent.getPayload();
                JsonNode tree = objectMapper.readTree(payload);
                String eventSource = tree.findValue("Records").get(0).get("eventSource").asText();
                if ("aws:sqs".equals(eventSource)) return SQS_FUN;
                else return KINESIS_FUN;
            } catch (Exception e) {
                String msg = "Failed to find suitable function for incoming event";
                throw new RuntimeException(msg, e);
            }
        }
    }
}

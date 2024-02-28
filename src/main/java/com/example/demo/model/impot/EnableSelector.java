package com.example.demo.model.impot;

import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurationSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MyImportSelector.class)
public @interface EnableSelector {
}

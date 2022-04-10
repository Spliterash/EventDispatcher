package ru.spliterash.eventDispatcher.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Включает автоматическую регистрацию бинов, которые имплементят EventListener
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Import(EventListenerBeanPostProcessor.class)
public @interface EnableEventDispatcher {
}

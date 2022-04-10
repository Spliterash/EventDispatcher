package ru.spliterash.eventDispatcher.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import ru.spliterash.eventDispatcher.EventDispatcher;
import ru.spliterash.eventDispatcher.event.EventListener;

@RequiredArgsConstructor
class EventListenerBeanPostProcessor implements BeanPostProcessor {
    private final EventDispatcher eventDispatcher;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EventListener)
            eventDispatcher.registerListener((EventListener<?>) bean);

        return bean;
    }
}

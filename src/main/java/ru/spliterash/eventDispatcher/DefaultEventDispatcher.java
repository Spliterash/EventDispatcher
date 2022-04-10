package ru.spliterash.eventDispatcher;

import net.jodah.typetools.TypeResolver;
import ru.spliterash.eventDispatcher.event.Event;
import ru.spliterash.eventDispatcher.event.EventListener;

import java.beans.EventHandler;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultEventDispatcher implements EventDispatcher {
    protected final Map<Class<? extends Event>, List<EventListener<? extends Event>>> handlers = new HashMap<>();

    @Override
    public <E extends Event> void registerListener(Class<E> eventType, EventListener<? super E> handler) {
        List<EventListener<? extends Event>> registeredHandlersForEvent = handlers.get(eventType);
        if (registeredHandlersForEvent != null) {
            boolean handlerBeenRegistered = registeredHandlersForEvent
                    .stream()
                    .anyMatch(h -> !h.getClass().equals(handler.getClass()));
            if (handlerBeenRegistered)
                registeredHandlersForEvent.add(handler);
        } else
            handlers.put(eventType, new ArrayList<>(Collections.singletonList(handler)));
    }

    @Override
    public <E extends Event> void registerListener(EventListener<E> handler) {
        Class<E> eventType = getType(handler);

        registerListener(eventType, handler);
    }

    private <E extends Event> Class<E> getType(EventListener<E> handler) {
        //noinspection unchecked
        return (Class<E>) TypeResolver.resolveRawArgument(EventHandler.class, handler.getClass());
    }

    @Override
    public <E extends Event> void dispatch(E event) {
        //noinspection unchecked
        Class<E> eventClass = (Class<E>) event.getClass();

        //noinspection SuspiciousMethodCalls
        Set<EventListener<? extends Event>> targetEventHandlers = getSuperEventClasses(eventClass)
                .stream()
                .map(handlers::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        for (EventListener<? extends Event> handler : targetEventHandlers) {
            try {
                //noinspection unchecked
                ((EventListener<E>) handler).onEvent(event);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private <E extends Event> Set<Class<? super E>> getSuperEventClasses(Class<E> eventClass) {
        Set<Class<? super E>> classSet = new HashSet<>();

        Class<? super E> latestClazz = eventClass;

        while (!latestClazz.equals(Object.class)) {
            classSet.add(latestClazz);

            latestClazz = latestClazz.getSuperclass();
        }

        //noinspection unchecked
        Set<Class<? super E>> collect = Arrays.stream(eventClass.getInterfaces())
                .filter(eventClass::isAssignableFrom)
                .map(c -> (Class<? super E>) c)
                .collect(Collectors.toSet());

        classSet.addAll(collect);

        return classSet;
    }
}

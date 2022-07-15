package ru.spliterash.eventDispatcher;

import lombok.RequiredArgsConstructor;
import net.jodah.typetools.TypeResolver;
import ru.spliterash.eventDispatcher.event.Event;
import ru.spliterash.eventDispatcher.event.EventListener;
import ru.spliterash.eventDispatcher.event.RegisteredListener;
import ru.spliterash.spcore.structure.spLinkedlist.SPLinkedList;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DefaultEventDispatcher implements EventDispatcher {
    protected final Map<Class<? extends Event>, SPLinkedList<EventListener<? extends Event>>> handlers = new HashMap<>();
    protected final Lock lock = new ReentrantLock();

    @Override
    public <E extends Event> RegisteredListener registerListener(Class<E> eventType, EventListener<? super E> listener) {
        lock.lock();
        try {
            SPLinkedList<EventListener<? extends Event>> registeredHandlersForEvent = handlers.computeIfAbsent(eventType, s -> new SPLinkedList<>());
            SPLinkedList.LinkedListElement<EventListener<? extends Event>> link = registeredHandlersForEvent.add(listener);

            return new DefaultRegisteredListener(eventType, link, registeredHandlersForEvent);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <E extends Event> RegisteredListener registerListener(EventListener<E> handler) {
        Class<E> eventType = getType(handler);

        return registerListener(eventType, handler);
    }

    private <E extends Event> Class<E> getType(EventListener<E> listener) {
        //noinspection unchecked
        return (Class<E>) TypeResolver.resolveRawArgument(EventListener.class, listener.getClass());
    }

    @Override
    public <E extends Event> void dispatch(E event) {
        lock.lock();
        try {
            //noinspection unchecked
            Class<E> eventClass = (Class<E>) event.getClass();
            //noinspection SuspiciousMethodCalls
            getSuperEventClasses(eventClass)
                    .stream()
                    .map(handlers::get)
                    .filter(Objects::nonNull)
                    .flatMap(s -> StreamSupport.stream(s.spliterator(), false))
                    .sorted(Comparator.comparingInt(EventListener::priority))
                    .forEachOrdered(eventListener -> {
                        try {
                            //noinspection unchecked
                            ((EventListener<E>) eventListener).onEvent(event);
                        } catch (Exception t) {
                            t.printStackTrace();
                        }
                    });
        } finally {
            lock.unlock();
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

    @RequiredArgsConstructor
    private final class DefaultRegisteredListener implements RegisteredListener {
        private final Class<? extends Event> clazz;

        private final SPLinkedList.LinkedListElement<?> link;
        private final SPLinkedList<EventListener<? extends Event>> anotherSameTypeListeners;

        @Override
        public void unregister() {
            lock.lock();
            try {
                link.remove();
                if (anotherSameTypeListeners.isEmpty())
                    handlers.remove(clazz);
            } finally {
                lock.unlock();
            }
        }
    }
}

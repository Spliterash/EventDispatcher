package ru.spliterash.eventDispatcher;

import ru.spliterash.eventDispatcher.event.Event;
import ru.spliterash.eventDispatcher.event.EventListener;

public interface EventDispatcher {
    <E extends Event> void registerListener(Class<E> eventType, EventListener<? super E> handler);

    <E extends Event> void registerListener(EventListener<E> handler);

    <E extends Event> void dispatch(E event);
}

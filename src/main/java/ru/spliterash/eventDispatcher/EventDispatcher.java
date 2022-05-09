package ru.spliterash.eventDispatcher;

import ru.spliterash.eventDispatcher.event.Event;
import ru.spliterash.eventDispatcher.event.EventListener;
import ru.spliterash.eventDispatcher.event.RegisteredListener;

public interface EventDispatcher {
    <E extends Event> RegisteredListener registerListener(Class<E> eventType, EventListener<? super E> handler);

    <E extends Event> RegisteredListener registerListener(EventListener<E> handler);

    <E extends Event> void dispatch(E event);
}

package ru.spliterash.eventDispatcher;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.spliterash.eventDispatcher.event.Event;
import ru.spliterash.eventDispatcher.event.EventListener;

public class EventDispatcherTests {
    /**
     * Тест проверяет что event обработался 2 раза, как мы его и кидали
     */
    @Test
    void DefaultEventDispatcher_RegisterOneListenerForMultiplyEvent_Successful() {
        EventDispatcher eventDispatcher = new DefaultEventDispatcher();
        eventDispatcher.registerListener(ParentEvent.class, new CallbackEventListener());

        Callback callback = Mockito.mock(Callback.class);

        eventDispatcher.dispatch(new ParentEvent(callback));
        eventDispatcher.dispatch(new ParentEvent(callback));

        Mockito.verify(callback, Mockito.times(2)).call();
    }

    /**
     * Проверяем что если установить класс event'а ниже чем Parent, то есть его наследуемый класс, то Parent
     * вызываться не будет
     * поскольку нас интересует конкретно ChildEvent
     */
    @Test
    void DefaultEventDispatcher_VerifyNoExcessEventFire() {
        EventDispatcher eventDispatcher = new DefaultEventDispatcher();

        // Слушаем только ChildEvent, и его наследников, ParentEvent тут не слушается
        eventDispatcher.registerListener(ChildEvent.class, new CallbackEventListener());

        Callback needCall = Mockito.mock(Callback.class);
        Callback doesNotCall = Mockito.mock(Callback.class);

        eventDispatcher.dispatch(new ChildEvent(needCall));
        eventDispatcher.dispatch(new ParentEvent(doesNotCall));

        Mockito.verify(needCall, Mockito.times(1)).call();
        Mockito.verify(doesNotCall, Mockito.times(0)).call();
    }

    /**
     * В этом тесте наоборот, ставим класс event ниже всего(CallbackEvent), и пытаемся вызвать разные евенты
     */
    @Test
    void DefaultEventDispatcher_VerifySuperEventFire() {
        EventDispatcher eventDispatcher = new DefaultEventDispatcher();

        // Слушаем всё что наследуется от CallbackEvent
        eventDispatcher.registerListener(CallbackEvent.class, new CallbackEventListener());

        Callback callback = Mockito.mock(Callback.class);

        eventDispatcher.dispatch(new ParentEvent(callback));
        eventDispatcher.dispatch(new ChildEvent(callback));

        // Проверяем что и там и там отработало
        Mockito.verify(callback, Mockito.times(2)).call();

    }

    @Test
    void DefaultEventDispatcher_DispatchEventWithStoreInDB_Successful() {
        EventDispatcher eventDispatcher = new DefaultEventDispatcher();


        eventDispatcher.registerListener(Test1Event.class, new SuperEventListener<>());
        eventDispatcher.registerListener(Test2Event.class, new SuperEventListener<>());

        eventDispatcher.dispatch(new Test1Event());
        eventDispatcher.dispatch(new Test2Event());
    }

    private abstract static class SuperEvent implements Event {

    }

    private static class Test1Event extends SuperEvent {

    }

    private static class Test2Event extends SuperEvent {

    }

    @RequiredArgsConstructor
    private abstract static class CallbackEvent extends SuperEvent {
        private final Callback callback;
    }

    private static class ParentEvent extends CallbackEvent {

        public ParentEvent(Callback callback) {
            super(callback);
        }
    }

    private static class ChildEvent extends ParentEvent {

        public ChildEvent(Callback callback) {
            super(callback);
        }
    }

    private static class SuperEventListener<T extends SuperEvent> implements EventListener<T> {

        @Override
        public void onEvent(T event) {
            System.out.println("Event " + event.getClass().getSimpleName() + " fired");
        }
    }

    private static class CallbackEventListener extends SuperEventListener<CallbackEvent> {

        @Override
        public void onEvent(CallbackEvent event) {
            super.onEvent(event);
            if (event.callback != null)
                event.callback.call();
        }
    }

    private interface Callback {
        void call();
    }
}

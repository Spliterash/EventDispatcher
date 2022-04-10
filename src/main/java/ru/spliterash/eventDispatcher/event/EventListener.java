package ru.spliterash.eventDispatcher.event;

public interface EventListener<E extends Event> {
    void onEvent(E event) throws Exception;

    /**
     * Приоритет больше - получает после всех
     * Приоритет меньше - получает раньше всех
     * Одинаковый - в порядке регистрации
     */
    default int priority() {
        return 0;
    }
}

package br.eng.moretto.cache.mappers;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ContainerizedType<T> {

    private final Class<? extends Collection<T>> container;
    private final Class<T> type;

    public ContainerizedType(final Class<? extends Collection<T>> container, final Class<T> type) {
        this.container = container;
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public Class<? extends Collection<T>> getContainer() {
        return container;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static public <T> ContainerizedType<T> listOfClass(final Class<T> type) {
        final Class<List<T>> container = (Class) List.class;
        return new ContainerizedType<T>(container, type);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static public <T> ContainerizedType<T> setOfClass(final Class<T> type) {
        final Class<Set<T>> container = (Class) Set.class;
        return new ContainerizedType<T>(container, type);
    }
}

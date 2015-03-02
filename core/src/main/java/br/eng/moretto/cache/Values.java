package br.eng.moretto.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Values<T> implements ValuesCollector<T> {
    private final HashSet<Value<T>> rawValues;

    public Values() {
        rawValues = new HashSet<Value<T>>();
    }

    public Values(final Collection<? extends Value<T>> c) {
        rawValues = new HashSet<Value<T>>(c);
    }

    public Values(final int initialCapacity) {
        rawValues = new HashSet<Value<T>>(initialCapacity);
    }

    public Map<Object, T> mapOfPresentValues() {
        return stream()
                .filter(v -> v.isPresent())
                .collect(Collectors.toMap(Value::getKey, Value::getValue));
    }

    public boolean hasPresentValues() {
        return stream().anyMatch(v -> v.isPresent());
    }

    public boolean hasEmptyValues() {
        return stream().anyMatch(v -> v.isEmpty());
    }

    @Override
    public void collect(final Object key, final T value) {
        for (final Value<T> wrapper : rawValues) {
            if (wrapper.getKey().equals(key)) {
                wrapper.setValue(value);
                return;
            }
        }
    }

    public void forEach(final Consumer<? super Value<T>> action) {
        rawValues.forEach(action);
    }

    public int size() {
        return rawValues.size();
    }

    public boolean isEmpty() {
        return rawValues.isEmpty();
    }

    public Stream<Value<T>> stream() {
        return rawValues.stream();
    }

    public Stream<Value<T>> parallelStream() {
        return rawValues.parallelStream();
    }

    public boolean add(final Value<T> e) {
        return rawValues.add(e);
    }

    public boolean addAll(final Collection<? extends Value<T>> c) {
        return rawValues.addAll(c);
    }
}
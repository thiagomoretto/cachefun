package br.eng.moretto.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class Values<T> extends HashSet<Value<T>> implements ValueCollector<T> {
    private static final long serialVersionUID = -7890760771090501901L;

    public Values() {
        super();
    }

    public Values(Collection<? extends Value<T>> c) {
        super(c);
    }

    public Values(int initialCapacity) {
        super(initialCapacity);
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
    public void collect(Object key, T value) {
        for (Value<T> wrapper : this) {
            if (wrapper.getKey().equals(key)) {
                wrapper.setValue(value);
                return;
            }
        }
    }
}
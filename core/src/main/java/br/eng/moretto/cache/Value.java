package br.eng.moretto.cache;


public class Value<K, T> implements ValueCollector<T> {
    static Value<Void, Void> voidValue = new Value<Void, Void>(null, null);

    public static Value<Void, Void> getVoidValue() {
        return voidValue;
    }

    private K key;
    private T value;

    public Value(final K key) {
        this(key, null);
    }

    public Value(final K key, final T value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    protected void setValue(final T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public boolean isPresent() {
        return getValue() != null;
    }

    public boolean isEmpty() {
        return !isPresent();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (key == null ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        final
        Value other = (Value) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    @Override
    public void collect(final T value) {
        setValue(value);
    }
}

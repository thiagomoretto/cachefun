package br.eng.moretto.cache;


public class Value<T> {
    static Value<Void> voidValue = new Value<Void>(null, null);

    public static Value<Void> getVoidValue() {
        return voidValue;
    }

    private Object key;
    private T value;

    public Value(final Object key) {
        this(key, null);
    }

    public Value(final Object key, final T value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(final Object key) {
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    public void setValue(final T value) {
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
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        final
        Value other = (Value) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }
}

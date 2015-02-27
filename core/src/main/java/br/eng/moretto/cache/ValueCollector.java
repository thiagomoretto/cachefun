package br.eng.moretto.cache;

@FunctionalInterface
public interface ValueCollector<T> {

    public void collect(Object id, T value);

}

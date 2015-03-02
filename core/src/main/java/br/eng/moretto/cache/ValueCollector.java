package br.eng.moretto.cache;

@FunctionalInterface
public interface ValueCollector<T> {

    public void collect(T value);

}

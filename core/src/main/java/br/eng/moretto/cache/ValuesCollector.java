package br.eng.moretto.cache;

@FunctionalInterface
public interface ValuesCollector<T> {

    public void collect(Object id, T value);

}

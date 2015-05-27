package br.eng.moretto.cache;

@FunctionalInterface
public interface ValuesCollector<K, T> {

    public void collect(K id, T value);

}

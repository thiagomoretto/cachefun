package br.eng.moretto.cache;


@FunctionalInterface
public interface FetchMany<K, T> {

    public void fetchMany(Values<K, T> ids);

}

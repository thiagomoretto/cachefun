package br.eng.moretto.cache;


@FunctionalInterface
public interface FetchOne<K, T> {

    public void fetchOne(Value<K, T> collector);

}

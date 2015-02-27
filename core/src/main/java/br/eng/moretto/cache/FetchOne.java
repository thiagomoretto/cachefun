package br.eng.moretto.cache;


@FunctionalInterface
public interface FetchOne<T> {

    public Value<T> fetchOne(Object id);

}

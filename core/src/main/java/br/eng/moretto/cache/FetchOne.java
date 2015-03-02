package br.eng.moretto.cache;


@FunctionalInterface
public interface FetchOne<T> {

    public void fetchOne(Value<T> collector);

}

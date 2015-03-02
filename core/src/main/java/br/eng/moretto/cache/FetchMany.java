package br.eng.moretto.cache;


@FunctionalInterface
public interface FetchMany<T> {

    public void fetchMany(Values<T> ids);

}

package br.eng.moretto.cache;

import java.util.Collection;

public interface Cache<K, T> {

    public Value<T> get(final K id);

    public Value<T> get(final K id, final FetchOne<T> fetchOne);

    public void put(final K id, final T object);

    public Values<T> getAll(final Collection<K> ids);

    public Values<T> getAll(final Collection<K> ids, final FetchMany<T> fetchMany);

    public Values<T> getAll(final Collection<K> ids, final FetchOne<T> fetchOne);

}

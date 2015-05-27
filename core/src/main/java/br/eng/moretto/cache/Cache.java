package br.eng.moretto.cache;

import java.util.Collection;

public interface Cache<K, T> {

    public Value<K, T> get(final K id);

    public Value<K, T> get(final K id, final FetchOne<K, T> fetchOne);

    public Values<K, T> getAll(final Collection<K> ids);

    public Values<K, T> getAll(final Collection<K> ids, final FetchMany<K, T> fetchMany);

    public Values<K, T> getAll(final Collection<K> ids, final FetchOne<K, T> fetchOne);

    public void put(final K id, final T object);

}

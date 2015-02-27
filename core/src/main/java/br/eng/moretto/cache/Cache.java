package br.eng.moretto.cache;

import java.util.Collection;

public interface Cache {

    public <T> Value<T> get(final Object id, final Class<T> clazz);

    public void put(final Object id, final Object object);

    public <T> Values<T> getAll(final Collection<Object> ids, final Class<T> clazz);

    public <T> Values<T> getAll(final Collection<Object> ids, final Class<T> clazz, final FetchMany<T> fetchMany);

    public <T> Values<T> getAll(final Collection<Object> ids, final Class<T> clazz, final FetchOne<T> fetchOne);

}

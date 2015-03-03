package br.eng.moretto.cache;

import java.util.Collection;

import br.eng.moretto.cache.ops.CacheOperations;

public class J8Cache<K, T> implements Cache<K, T> {
    private final CacheOperations cacheOperations;
    private final Mapper<T> mapper;

    public J8Cache(final CacheOperations cacheOperations, final Mapper<T> mapper) {
        this.cacheOperations = cacheOperations;
        this.mapper = mapper;
    }

    @Override
    public Value<T> get(final K id) {
        return cacheOperations.get(id, mapper);
    }

    @Override
    public void put(final K id, final T object) {
        cacheOperations.put(id, object, mapper);
    }

    @Override
    public Values<T> getAll(final Collection<K> ids) {
        return cacheOperations.execute(ops -> //
                ids.stream().map(id -> ops.get(id, mapper)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Values<T> getAll(final Collection<K> ids, final FetchMany<T> fetchMany) {
        final Values<T> values = getAll(ids);
        final Values<T> missing = new Values<T>();
        values.stream() //
                .filter(v -> v.isEmpty()) //
                .forEach(v -> missing.add(v)); // added as ref.
        if (missing.size() > 0) {
            fetchMany.fetchMany(missing);
            missing.stream() //
                    .filter(v -> v.isPresent()) //
                    .forEach(v -> put((K)v.getKey(), v.getValue())); // TODO:
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Values<T> getAll(final Collection<K> ids, final FetchOne<T> fetchOne) {
        final Values<T> values = getAll(ids);
        values.stream() //
                .filter(v -> v.isEmpty()) //
                .map(v -> { //
                    fetchOne.fetchOne(v); //
                    return v; //
                }) //
                .filter(v -> v.isPresent()) //
                .forEach(v -> { //
                            values.collect(v.getKey(), v.getValue()); //
                            put((K)v.getKey(), v.getValue()); //
                        });
        return values;
    }

    @Override
    public Value<T> get(final K id, final FetchOne<T> fetchOne) {
        final Value<T> value = get(id);
        if (value.isEmpty()) {
            fetchOne.fetchOne(value);
            if (value.isPresent()) {
                put(id, value.getValue());
            }
        }
        return value;
    }
}

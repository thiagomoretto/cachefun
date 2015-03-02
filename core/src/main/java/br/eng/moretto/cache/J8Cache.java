package br.eng.moretto.cache;

import java.util.Collection;
import java.util.stream.Collectors;

import br.eng.moretto.cache.ops.CacheOperations;

public class J8Cache<K, T> implements Cache<K, T> {
    private final CacheOperations cacheOperations;
    private final Class<T> clazz;

    public J8Cache(final CacheOperations cacheOperations, final Class<T> clazz) {
        this.cacheOperations = cacheOperations;
        this.clazz = clazz;
    }

    @Override
    public Value<T> get(final K id) {
        return cacheOperations.get(id, clazz);
    }

    @Override
    public void put(final Object id, final Object object) {
        cacheOperations.put(id, object);
    }

    @Override
    public Values<T> getAll(final Collection<K> ids) {
        return cacheOperations.execute((final CacheOperations ops) ->
                    ids.stream().map(id -> ops.get(id, clazz)));
    }

    @Override
    public Values<T> getAll(final Collection<K> ids, final FetchMany<T> fetchMany) {
        final Values<T> values = cacheOperations.execute((final CacheOperations ops) ->
                                        ids.stream().map(id -> ops.get(id, clazz)));
        final Values<T> missing = values.stream()
                .filter(v -> v.isEmpty())
                .collect(Collectors.toCollection(() -> new Values<T>()));
        if(missing.size() > 0) {
            fetchMany.fetchMany(missing, missing);
            missing.stream()
                    .filter(v -> v.isPresent())
                    .forEach(v -> put(v.getKey(), v.getValue()));
        }
        return values;
    }

    @Override
    public Values<T> getAll(final Collection<K> ids, final FetchOne<T> fetchOne) {
        final Values<T> values = getAll(ids);
        values.stream()
                .filter(v -> v.isEmpty())
                .map(v -> fetchOne.fetchOne(v.getKey()))
                .filter(v -> v.isPresent())
                .forEach(v -> {
                    values.collect(v.getKey(), v.getValue());
                    put(v.getKey(), v.getValue());
                });
        return values;
    }

    @Override
    public Value<T> get(final K id, final FetchOne<T> fetchOne) {
        final Value<T> value = cacheOperations.get(id, clazz);
        if(value.isEmpty()) {
            value.setValue(fetchOne.fetchOne(id).getValue());
            put(id, value.getValue());
        }
        return value;
    }
}

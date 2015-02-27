package br.eng.moretto.cache;

import java.util.Collection;
import java.util.stream.Collectors;

import br.eng.moretto.cache.ops.CacheOperations;

public class J8Cache implements Cache {
    private final CacheOperations cacheOperations;

    public J8Cache(final CacheOperations cacheOperations) {
        this.cacheOperations = cacheOperations;
    }

    @Override
    public <T> Value<T> get(final Object id, final Class<T> clazz) {
        return cacheOperations.get(id, clazz);
    }

    @Override
    public void put(final Object id, final Object object) {
        cacheOperations.put(id, object);
    }

    @Override
    public <T> Values<T> getAll(final Collection<Object> ids, final Class<T> clazz) {
        return cacheOperations.execute((final CacheOperations ops) ->
                    ids.stream().map(id -> ops.get(id, clazz)));
    }

    @Override
    public <T> Values<T> getAll(final Collection<Object> ids, final Class<T> clazz, final FetchMany<T> fetchMany) {
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
    public <T> Values<T> getAll(final Collection<Object> ids, final Class<T> clazz, final FetchOne<T> fetchOne) {
        final Values<T> values = getAll(ids, clazz);
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
}

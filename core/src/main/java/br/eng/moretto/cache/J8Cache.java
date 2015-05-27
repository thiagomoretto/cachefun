package br.eng.moretto.cache;

import java.util.Collection;

import br.eng.moretto.cache.ops.CacheOperations;

public class J8Cache<K, T> implements Cache<K, T> {
    private final CacheOperations cacheOperations;
    private final Mapper<K> keyMapper;
    private final Mapper<T> valMapper;

    public J8Cache(final CacheOperations cacheOperations, final Mapper<K> keyMapper, final Mapper<T> valMapper) {
        this.cacheOperations = cacheOperations;
        this.keyMapper = keyMapper;
        this.valMapper = valMapper;
    }

    @Override
    public Value<K, T> get(final K id) {
        final Value<String, T> raw = cacheOperations.get(keyMapper.write(id), valMapper);
        return new Value<K, T>(id, raw.getValue());
    }

    @Override
    public void put(final K id, final T object) {
        cacheOperations.put(keyMapper.write(id), object, valMapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Values<K, T> getAll(final Collection<K> ids) {
        return cacheOperations.execute(ops -> //
            ids.stream().map(id ->
                (Value<K, T>) ops.get(keyMapper.write(id), valMapper)));
    }

    @Override
    public Values<K, T> getAll(final Collection<K> ids, final FetchMany<K, T> fetchMany) {
        final Values<K, T> values = getAll(ids);
        final Values<K, T> missing = new Values<K, T>();
        values.stream() //
                .filter(v -> v.isEmpty()) //
                .forEach(v -> missing.add(v)); // added as ref.
        if (missing.size() > 0) {
            fetchMany.fetchMany(missing);
            missing.stream() //
                    .filter(v -> v.isPresent()) //
                    .forEach(v -> put(v.getKey(), v.getValue())); // TODO:
        }
        return values;
    }

    @Override
    public Values<K, T> getAll(final Collection<K> ids, final FetchOne<K, T> fetchOne) {
        final Values<K, T> values = getAll(ids);
        values.stream() //
                .filter(v -> v.isEmpty()) //
                .map(v -> { //
                    fetchOne.fetchOne(v); //
                    return v; //
                }) //
                .filter(v -> v.isPresent()) //
                .forEach(v -> { //
                            values.collect(v.getKey(), v.getValue()); //
                            put(v.getKey(), v.getValue()); //
                        });
        return values;
    }

    @Override
    public Value<K, T> get(final K id, final FetchOne<K, T> fetchOne) {
        final Value<K, T> value = get(id);
        if (value.isEmpty()) {
            fetchOne.fetchOne(value);
            if (value.isPresent()) {
                put(id, value.getValue());
            }
        }
        return value;
    }
}

package br.eng.moretto.cache;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import br.eng.moretto.cache.ops.CacheOperations;

/**
 * Experimental API.
 *
 * @author tmoret1
 *
 * @param <K> The key type.
 * @param <T> The value type.
 */
public class CacheFuns<K, T> {
    private final CacheOperations cacheOperations;
    private final Mapper<T> valMapper;
    private final Mapper<K> keyMapper;

    public CacheFuns(final CacheOperations cacheOperations, final Mapper<K> keyMapper, final Mapper<T> valMapper) {
        this.cacheOperations = cacheOperations;
        this.keyMapper = keyMapper;
        this.valMapper = valMapper;
    }

    public Optional<T> get(final K id) {
        return Optional.ofNullable(cacheOperations.get(keyMapper.write(id), valMapper).getValue());
    }

    public Values<T> getAll(final Collection<K> ids) {
        return cacheOperations.execute(ops -> //
                ids.stream().map(id -> ops.get(keyMapper.write(id), valMapper)));
    }

    public void put(final K id, final T object) {
        cacheOperations.put(keyMapper.write(id), object, valMapper);
    }

    public Supplier<Optional<T>> get(final K id, final Supplier<Optional<T>> supplier) {
        return get(id, (_id) -> supplier.get());
    }

    public Supplier<Optional<T>> get(final K id, final Function<K, Optional<T>> fetch) {
        return () -> {
            Optional<T> val = get(id);
            if (!val.isPresent()) {
                val = fetch.apply(id);
                val.ifPresent(v -> put(id, v));
            }
            return val;
        };
    }

    public Stream<Optional<T>> streamOf(final Collection<K> ids) {
        final Values<T> values = cacheOperations.execute(ops -> //
                ids.stream() //
                   .map(id -> ops.get(keyMapper.write(id), valMapper)));
        return values.stream().map(t -> Optional.ofNullable(t.getValue()));
    }

    @SuppressWarnings("unchecked")
    public Stream<Optional<T>> streamOf(final Collection<K> ids, final Function<K, Optional<T>> fetch) {
        final Values<T> values = getAll(ids);
        values.stream() //
                .filter(v -> v.isEmpty()) //
                .map(v -> { //
                    fetch.apply((K) v.getKey()) //
                         .ifPresent(k -> v.collect(k));
                    return v;
                }) //
                .filter(v -> v.isPresent()) //
                .forEach(v -> { //
                            values.collect(v.getKey(), v.getValue()); //
                            put((K) v.getKey(), v.getValue()); //
                        });
        return values.stream().map(t -> Optional.ofNullable(t.getValue()));
    }
}

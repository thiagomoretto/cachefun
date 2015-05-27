package br.eng.moretto.cache;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

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
}

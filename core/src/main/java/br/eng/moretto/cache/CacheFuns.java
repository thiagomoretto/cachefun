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

    public void put(final K id, final T object) {
        cacheOperations.put(keyMapper.write(id), object, valMapper);
    }

    public Supplier<Optional<T>> get(final K id, final Function<K, T> fetch) {
        return () -> {
            final Value<T> value = cacheOperations.get(keyMapper.write(id), valMapper);
            T val = value.getValue();
            if (val == null) {
                val = fetch.apply(id);
                if (val != null) {
                    put(id, val);
                }
            }
            return Optional.ofNullable(val);
        };
    }
}

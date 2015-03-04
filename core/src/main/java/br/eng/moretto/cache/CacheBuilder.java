package br.eng.moretto.cache;

import br.eng.moretto.cache.ops.CacheOperations;


public class CacheBuilder<K, V> {
    private Mapper<K> keyMapper;
    private Mapper<V> valueMapper;
    private CacheOperations cacheOperations;

    public CacheBuilder<K, V> setKeyMapper(final Mapper<K> keyMapper) {
        this.keyMapper = keyMapper;
        return this;
    }

    public CacheBuilder<K, V> setValueMapper(final Mapper<V> valueMapper) {
        this.valueMapper = valueMapper;
        return this;
    }

    public CacheBuilder<K, V> setCacheOperations(final CacheOperations cacheOperations) {
        this.cacheOperations = cacheOperations;
        return this;
    }

    public Cache<K, V> build() {
        return new J8Cache<K, V>(cacheOperations, keyMapper, valueMapper);
    }
}

package br.eng.moretto.cache;

import java.util.stream.Stream;

import br.eng.moretto.cache.ops.CacheOperations;

@FunctionalInterface
public interface Commands<K, T> {

    public Stream<Value<K, T>> execute(CacheOperations operations);

}

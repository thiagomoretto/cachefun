package br.eng.moretto.cache;

import java.util.stream.Stream;

import br.eng.moretto.cache.ops.CacheOperations;

@FunctionalInterface
public interface Commands<T> {

    public Stream<Value<T>> execute(CacheOperations operations);

}

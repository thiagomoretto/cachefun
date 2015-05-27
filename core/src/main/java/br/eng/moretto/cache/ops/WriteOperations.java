package br.eng.moretto.cache.ops;

import br.eng.moretto.cache.Mapper;
import br.eng.moretto.cache.Value;


public interface WriteOperations {

    public <K, T> Value<K, Void> put(K id, T object, Mapper<T> mapper);

}

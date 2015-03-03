package br.eng.moretto.cache.ops;

import br.eng.moretto.cache.Mapper;
import br.eng.moretto.cache.Value;



public interface ReadOperations {

    public <K, T> Value<T> get(K id, Mapper<T> mapper);

}

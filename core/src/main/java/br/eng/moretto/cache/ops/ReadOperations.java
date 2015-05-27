package br.eng.moretto.cache.ops;

import br.eng.moretto.cache.Mapper;
import br.eng.moretto.cache.Value;



public interface ReadOperations {

    public <T> Value<String, T> get(String id, Mapper<T> mapper);

}

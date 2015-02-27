package br.eng.moretto.cache.ops;

import br.eng.moretto.cache.Value;


public interface ReadOperations {

    public <T> Value<T> get(Object id, Class<T> klass);

}

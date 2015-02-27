package br.eng.moretto.cache.ops;

import br.eng.moretto.cache.Value;


public interface WriteOperations {

    public Value<Void> put(Object id, Object object);

}

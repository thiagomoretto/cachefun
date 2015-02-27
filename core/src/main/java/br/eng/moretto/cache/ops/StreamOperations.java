package br.eng.moretto.cache.ops;

import br.eng.moretto.cache.Commands;
import br.eng.moretto.cache.Values;


public interface StreamOperations {

    public <T> Values<T> execute(Commands<T> commands);

}

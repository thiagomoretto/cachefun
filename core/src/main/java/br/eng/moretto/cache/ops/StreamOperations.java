package br.eng.moretto.cache.ops;

import br.eng.moretto.cache.Commands;
import br.eng.moretto.cache.Values;


public interface StreamOperations {

    public <K, T> Values<K, T> execute(Commands<K, T> commands);

}

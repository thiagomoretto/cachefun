package br.eng.moretto.cache;

import java.util.Collection;

// TODO: Rename.
public interface Serializer {

    public String serialize(Object object);

    public <T> T deserialize(String json, Class<T> klass);

    public <T, C extends Collection<T>> C deserialize(String json, Class<T> klass, Class<C> container);
}

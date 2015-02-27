package br.eng.moretto.cache.serializers;


import java.util.Collection;

import org.boon.json.JsonParserFactory;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;
import org.boon.json.implementation.ObjectMapperImpl;

import br.eng.moretto.cache.Serializer;

public class BoonSerializer implements Serializer {
    final ObjectMapper objectMapper;

    public BoonSerializer() {
        final JsonParserFactory parserFactory = new JsonParserFactory();
        parserFactory.setUseAnnotations(true);
        parserFactory.setRespectIgnore(true);
        final JsonSerializerFactory serializerFactory = new JsonSerializerFactory();
        serializerFactory.setUseAnnotations(true);
        serializerFactory.setIncludeEmpty(true);
        serializerFactory.setIncludeNulls(false);
        serializerFactory.setIncludeDefault(true);
        serializerFactory.setJsonFormatForDates(true);
        objectMapper
            = new ObjectMapperImpl(parserFactory, serializerFactory);
    }

    @Override
    public String serialize(final Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @Override
    public <T> T deserialize(final String json, final Class<T> klass) {
        return objectMapper.readValue(json, klass);
    }

    @Override
    public <T, C extends Collection<T>> C deserialize(final String json, final Class<T> klass, final Class<C> container) {
        return objectMapper.readValue(json, container, klass);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BoonSerializer [objectMapper=");
        builder.append(objectMapper);
        builder.append("]");
        return builder.toString();
    }
}
package br.eng.moretto.cache.serializers;


import org.boon.json.JsonParserFactory;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;
import org.boon.json.implementation.ObjectMapperImpl;

import br.eng.moretto.cache.Mapper;

public class BoonMapper<T> implements Mapper<T> {
    final ObjectMapper objectMapper;
    private final Class<T> clazz;

    public BoonMapper(final Class<T> clazz) {
        this.clazz = clazz;

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
    public String write(final T object) {
        return objectMapper.writeValueAsString(object);
    }

    @Override
    public T read(final String json) {
        return objectMapper.readValue(json, clazz);
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
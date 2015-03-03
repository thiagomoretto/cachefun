package br.eng.moretto.cache.serializers;


import org.boon.json.JsonParserFactory;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;
import org.boon.json.implementation.ObjectMapperImpl;

import br.eng.moretto.cache.Mapper;

public class BoonCollectionMapper<T, C> implements Mapper<C> {
    final ObjectMapper objectMapper;
    private final ContainerizedType<T> type;

    public BoonCollectionMapper(final ContainerizedType<T> type) {
        this.type = type;

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
    public String write(final C object) {
        return objectMapper.writeValueAsString(object);
    }

    @Override @SuppressWarnings("unchecked")
    public C read(final String json) {
         return (C) objectMapper.readValue(json, type.getContainer(), type.getType());
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
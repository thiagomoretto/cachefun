package br.eng.moretto.cache.redis;

import br.eng.moretto.cache.Serializer;
import br.eng.moretto.cache.Value;

public class JedisValue<T, J> extends Value<T> {
    final private redis.clients.jedis.Response<J> jedisResponse;
    final private Serializer serializer;
    final private Class<T> klass;

    public JedisValue(final Object key, final redis.clients.jedis.Response<J> jedisResponse,
                final Serializer serializer, final Class<T> klass) {
        super(key);
        this.jedisResponse = jedisResponse;
        this.serializer = serializer;
        this.klass = klass;
    }

    @Override
    public T getValue() {
        T value = super.getValue(); // Checks if we already have it.
        if(value == null) {
            final J jedisData = jedisResponse.get();
            if(jedisData instanceof String) {
                final String json = (String) jedisResponse.get();
                if(json != null) {
                    value = serializer.deserialize(json, klass);
                    setValue(value);
                }
            }
        }
        return value;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("JedisValue [getValue()=");
        builder.append(getValue());
        builder.append(", getKey()=");
        builder.append(getKey());
        builder.append("]");
        return builder.toString();
    }
}
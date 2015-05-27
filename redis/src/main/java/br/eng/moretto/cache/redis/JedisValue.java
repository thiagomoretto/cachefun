package br.eng.moretto.cache.redis;

import br.eng.moretto.cache.Mapper;
import br.eng.moretto.cache.Value;

public class JedisValue<K, T> extends Value<K, T> {
    final private redis.clients.jedis.Response<String> jedisResponse;
    final private Mapper<T> mapper;

    public JedisValue(final K key, final redis.clients.jedis.Response<String> jedisResponse, final Mapper<T> mapper) {
        super(key);
        this.jedisResponse = jedisResponse;
        this.mapper = mapper;
    }

    @Override
    public T getValue() {
        T value = super.getValue(); // Checks if we already have it.
        if(value == null) {
            final String jedisData = jedisResponse.get();
            if(jedisData != null) {
                value = mapper.read(jedisData);
                collect(value);
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
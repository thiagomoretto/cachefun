package br.eng.moretto.cache.redis;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;
import br.eng.moretto.cache.Commands;
import br.eng.moretto.cache.Serializer;
import br.eng.moretto.cache.Value;
import br.eng.moretto.cache.Values;
import br.eng.moretto.cache.ops.CacheOperations;

public class RedisCacheOperations implements CacheOperations {
    final private JedisPool pool;
    final private Serializer serializer;

    public RedisCacheOperations(final JedisPool jedisPool, final Serializer serializer) {
        this.pool = jedisPool;
        this.serializer = serializer;
    }

    @Override
    public <T> Value<T> get(final Object id, final Class<T> klass) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            final String data = jedis.get(String.valueOf(id));
            return new Value<T>(id, serializer.deserialize(data, klass));
        } catch(final JedisConnectionException jce) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if(jedis != null) {
                pool.returnResource(jedis);
                jedis = null;
            }
        }
        return new Value<T>(id, null);
    }

    @Override
    public Value<Void> put(final Object id, final Object object) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(String.valueOf(id), serializer.serialize(object));
        } catch(final JedisConnectionException jce) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if(jedis != null) {
                pool.returnResource(jedis);
                jedis = null;
            }
        }
        return new Value<Void>(null);
    }

    @Override
    public <T> Values<T> execute(final Commands<T> commands) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            final Pipeline pipeline = jedis.pipelined();
            final Stream<Value<T>> stream
                    = commands.execute(new PipelinedOperations(pipeline, serializer));
            final Collection<Value<T>> values = stream.collect(Collectors.toList());
            pipeline.sync();
            return new Values<>(values);
        } catch(final JedisConnectionException jce) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            return new Values<>(0);
        } finally {
            if(jedis != null) {
                pool.returnResource(jedis);
                jedis = null;
            }
        }
    }

    // Asynchronous operations
    final private class PipelinedOperations implements CacheOperations {

        final private Serializer serializer;
        final private Pipeline pipeline;

        public PipelinedOperations(final Pipeline pipeline, final Serializer serializer) {
            this.pipeline = pipeline;
            this.serializer = serializer;
        }

        @Override
        public <T> Value<T> get(final Object key, final Class<T> klass) {
            return new JedisValue<T, String>(key, pipeline.get(String.valueOf(key)), serializer, klass);
        }

        @Override
        public Value<Void> put(final Object key, final Object value) {
            final String data = serializer.serialize(value);
            return new JedisValue<>(key, pipeline.set(String.valueOf(key), data), null, Void.class);
        }

        @Override
        public <T> Values<T> execute(final Commands<T> commands) {
            return new Values<>(commands.execute(this).collect(Collectors.toList()));
        }
    }
}

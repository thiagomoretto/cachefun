package br.eng.moretto.cache.redis;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;
import br.eng.moretto.cache.Commands;
import br.eng.moretto.cache.Mapper;
import br.eng.moretto.cache.Value;
import br.eng.moretto.cache.Values;
import br.eng.moretto.cache.ops.CacheOperations;

public class RedisCacheOperations implements CacheOperations {
    final private JedisPool pool;

    public RedisCacheOperations(final JedisPool jedisPool) {
        this.pool = jedisPool;
    }

    @Override
    public <T> Value<String, T> get(final String id, final Mapper<T> mapper) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            final String data = jedis.get(String.valueOf(id));
            return new Value<String, T>(id, data != null ? mapper.read(data) : null);
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
        return new Value<String, T>(id, (T)null);
    }

    @Override
    public <K, T> Value<K, Void> put(final K id, final T object, final Mapper<T> mapper) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(String.valueOf(id), mapper.write(object));
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
        return new Value<K, Void>(id, (Void)null);
    }

    @Override
    public <K, T> Values<K, T> execute(final Commands<K, T> commands) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            final Pipeline pipeline = jedis.pipelined();
            final Stream<Value<K, T>> stream
                    = commands.execute(new PipelinedOperations(pipeline));
            final Collection<Value<K, T>> values = stream.collect(Collectors.toList());
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
        final private Pipeline pipeline;

        public PipelinedOperations(final Pipeline pipeline) {
            this.pipeline = pipeline;
        }

        @Override
        public <T> Value<String, T> get(final String key, final Mapper<T> mapper) {
            return new JedisValue<String, T>(key, pipeline.get(String.valueOf(key)), mapper);
        }

        @Override
        public <K, T> Value<K, Void> put(final K key, final T object, final Mapper<T> mapper) {
            pipeline.set(String.valueOf(key), mapper.write(object));
            return new Value<K, Void>(key, (Void)null);
        }

        @Override
        public <K, T> Values<K, T> execute(final Commands<K, T> commands) {
            return new Values<>(commands.execute(this).collect(Collectors.toList()));
        }
    }
}

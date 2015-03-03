package br.eng.moretto.cache.redis;

import redis.clients.jedis.JedisPool;
import br.eng.moretto.cache.BaseCacheTest;

public class RedisCacheTest extends BaseCacheTest {

    @Override
    protected RedisCacheOperations getCacheOperationsProvider() {
        return new RedisCacheOperations(new JedisPool("localhost"));
    }
}

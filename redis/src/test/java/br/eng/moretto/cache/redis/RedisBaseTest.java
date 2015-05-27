package br.eng.moretto.cache.redis;

import java.util.Random;

import redis.clients.jedis.JedisPool;
import br.eng.moretto.cache.CacheFuns;
import br.eng.moretto.cache.StringKeyMapper;
import br.eng.moretto.cache.mappers.BoonMapper;

public class RedisBaseTest {

    // Caches

    protected CacheFuns<String, String> cache(final String name) {
        return new CacheFuns<String, String>( //
                new RedisCacheOperations(new JedisPool("localhost")), //
                new StringKeyMapperPrefixed(name), //
                new BoonMapper<String>(String.class));
    }

    // Test mapper.

    class StringKeyMapperPrefixed extends StringKeyMapper {
        private final String prefix;
        private final int random = new Random().nextInt();
        public StringKeyMapperPrefixed(final String prefix) {
            this.prefix = prefix;
        }
        @Override
        public String write(final String object) {
            return new StringBuffer() //
                    .append(prefix) //
                    .append(":") //
                    .append(random) //
                    .append(":") //
                    .append(super.write(object)) //
                    .toString();
        }
    }
}

package br.eng.moretto.cache.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.JedisPool;
import br.eng.moretto.cache.CacheFuns;
import br.eng.moretto.cache.StringKeyMapper;
import br.eng.moretto.cache.mappers.BoonMapper;

public class RedisCacheFunsTest {
    private CacheFuns<String, String> funs;
    private int seed;

    @Before
    public void setup() {
        seed = new Random().nextInt();
        funs = new CacheFuns<String, String>( //
                new RedisCacheOperations(new JedisPool("localhost")), //
                new StringKeyMapper(), //
                new BoonMapper<String>(String.class));
    }

    @Test
    public void testGetFun() {
        final Supplier<Optional<String>> supplier = funs.get("key#" + seed,
                (id) -> "Value>" + id);

        assertNotNull(supplier.get().isPresent());
        assertEquals("Value>key#" + seed, supplier.get().get());
    }
}

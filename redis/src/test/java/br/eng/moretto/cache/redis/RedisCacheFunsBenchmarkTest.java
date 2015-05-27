package br.eng.moretto.cache.redis;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.JedisPool;
import br.eng.moretto.cache.BaseCacheTest.Item;
import br.eng.moretto.cache.CacheFuns;
import br.eng.moretto.cache.mappers.BoonMapper;

public class RedisCacheFunsBenchmarkTest extends RedisBaseTest {
    private CacheFuns<String, Item> main;
    private String prefix;

    @Before
    public void init() {
        prefix = String.valueOf(new Random().nextInt());
        main = new CacheFuns<String, Item>( //
                new RedisCacheOperations(new JedisPool("localhost")), //
                new StringKeyMapperPrefixed(prefix), //
                new BoonMapper<Item>(Item.class));
    }

    @Test
    public void testGetAllWithFetchN() throws Exception {
        runGetAllBench("warmup", 5_000);
        runGetAllBench("real deal", 5_000);
    }

    private void runGetAllBench(final String name, final int nSamples) {
        final Collection<String> ids = new ArrayList<>();
        for (int sample = 0; sample < nSamples; sample++) {
            String id;
            ids.add(id = "FetchN:Item" + sample);
            main.put(id, new Item("Item" + sample));
        }

        final long initial = System.nanoTime();

        assertEquals(nSamples, //
                main.streamOf(ids) //
                        .filter(v -> v.isPresent()) //
                        .count());

        final long ns = System.nanoTime() - initial;
        final long ms = ns / 1_000_000;

        System.out.println("RedisCacheFunsBenchmarkTest#runGetAllBench (samples=" + nSamples + "): '" + name + "' = " + ns + "ns / " + ms + "ms");
        System.out.println("RedisCacheFunsBenchmarkTest#runGetAllBench " + nSamples / (ms / 100.0) + " op/s");
    }

    // Utils

    public String d(final String key) {
        return new StringBuilder(prefix).append(key).toString();
    }
}

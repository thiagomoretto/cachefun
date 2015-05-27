package br.eng.moretto.cache.redis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

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
        runGetAllBench("warmup_1", 5_000);
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

        final Stream<Optional<Item>> stream = main.streamOf(ids);

        final long ns = System.nanoTime() - initial;
        final long ms = ns / 1_000_000;

        assertThat(stream.filter(v -> v.isPresent()).count()).isEqualTo(nSamples);

        System.out.println("RedisCacheFunsBenchmarkTest#runGetAllBench (samples=" + nSamples + "): '" + name + "' = " + ns + "ns / " + ms + "ms");
        System.out.println("RedisCacheFunsBenchmarkTest#runGetAllBench " + nSamples / (ms / 100.0) + " op/s");
    }

    // Utils

    public String d(final String key) {
        return new StringBuilder(prefix).append(key).toString();
    }
}

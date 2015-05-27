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
    private final CacheFuns<String, String> funsL1 = l1Cache();
    private final CacheFuns<String, String> funsL2 = l2Cache();

    @Before
    public void setup() {
        funs = new CacheFuns<String, String>( //
                new RedisCacheOperations(new JedisPool("localhost")), //
                new StringKeyMapperPrefixed("main"), //
                new BoonMapper<String>(String.class));
    }

    @Test
    public void testGetFun() {
        final Supplier<Optional<String>> supplier = funs.get("key1",
                (id) -> "Value1");

        assertNotNull(supplier.get().isPresent());
        assertEquals("Value1", supplier.get().get());
    }

    @Test
    public void testL1L2UsingSupplierApi() {
        final String key = "testL1L2UsingSupplierApi";

        funsL2.put(key, "Value#testL1L2UsingSupplierApi");

        final Supplier<Optional<String>> l1l2Supplier = //
                funsL1.get(key, //
                funsL2.get(key, () -> Optional.of("From database")));

        final Optional<String> value = l1l2Supplier.get();

        assertNotNull(value.isPresent());
        assertEquals("Value#testL1L2UsingSupplierApi", value.get());
    }

    @Test
    public void testL1L2UsingFunctionApi() {
        final String key = "testL1L2UsingFunctionApi";

        funsL2.put(key, "Value#testL1L2UsingFunctionApi");

        final Optional<String> value = //
                funsL1.get(key, (l1Key) -> //
                    funsL2.get(l1Key, (l2Key) -> "From database").get().get()).get();

        assertNotNull(value.isPresent());
        assertEquals("Value#testL1L2UsingFunctionApi", value.get());
    }

    private CacheFuns<String, String> l2Cache() {
        final CacheFuns<String, String> funsL2 = new CacheFuns<String, String>( //
                new RedisCacheOperations(new JedisPool("localhost")), //
                new StringKeyMapperPrefixed("l2"), //
                new BoonMapper<String>(String.class));
        return funsL2;
    }

    private CacheFuns<String, String> l1Cache() {
        final CacheFuns<String, String> funsL1 = new CacheFuns<String, String>( //
                new RedisCacheOperations(new JedisPool("localhost")), //
                new StringKeyMapperPrefixed("l1"), //
                new BoonMapper<String>(String.class));
        return funsL1;
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

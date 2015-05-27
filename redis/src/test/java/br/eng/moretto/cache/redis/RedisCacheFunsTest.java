package br.eng.moretto.cache.redis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Test;

import redis.clients.jedis.JedisPool;
import br.eng.moretto.cache.CacheFuns;
import br.eng.moretto.cache.StringKeyMapper;
import br.eng.moretto.cache.mappers.BoonMapper;

public class RedisCacheFunsTest {
    private final CacheFuns<String, String> main = cache("main");
    private final CacheFuns<String, String> l1 = cache("l1");
    private final CacheFuns<String, String> l2 = cache("l2");

    @Test
    public void testGetFun() {
        assertThat(main.get("key1", (id) -> Optional.of("Value1")).get()) //
            .isPresent() //
            .contains("Value1");
    }

    @Test
    public void testL1L2UsingSupplierApi() {
        final String key = "testL1L2UsingSupplierApi";

        l2.put(key, "Value#testL1L2UsingSupplierApi");
        final Supplier<Optional<String>> l1l2Supplier = //
                l1.get(key, //
                l2.get(key, () -> Optional.of("From database")));

        assertThat(l1l2Supplier.get())
            .isPresent() //
            .contains("Value#testL1L2UsingSupplierApi");
    }

    @Test
    public void testL1L2UsingSupplierApiMisses() {
        final String key = "testL1L2UsingSupplierApiUncached";
        final Supplier<Optional<String>> database = () -> Optional.of("From database");
        final Supplier<Optional<String>> l1l2Supplier = //
                l1.get(key, l2.get(key, database));

        assertThat(l1l2Supplier.get())
            .isPresent() //
            .contains("From database");
    }

    @Test
    public void testL1L2UsingFunctionApi() {
        final String key = "testL1L2UsingFunctionApi";
        l2.put(key, "Value#testL1L2UsingFunctionApi");

        final Supplier<Optional<String>> l1l2Supplier = //
                l1.get(key, (l1Key) -> //
                l2.get(l1Key, (l2Key) -> Optional.of("From database")).get());

        assertThat(l1l2Supplier.get()) //
            .isPresent() //
            .contains("Value#testL1L2UsingFunctionApi");
    }

    @Test
    public void testStream() throws Exception {
        main.put("Key1", "Value1");
        main.put("Key2", "Value2");
        main.put("Key3", "Value3");

        assertThat(main.streamOf(Arrays.asList("Key1", "Key2", "Key3"))
            .allMatch(p -> p.isPresent()))
            .isTrue();
        assertThat(main.streamOf(Arrays.asList("Key1", "Key2", "Key3"))
            .collect(Collectors.toList()))
            .hasSize(3);
    }

    @Test
    public void testStreamWithSupplier() throws Exception {
        main.put("mKey1", "Value1");
        // Missing mKey2
        main.put("mKey3", "Value3");
        // Missing mKey4

        assertThat(main.streamOf(Arrays.asList("mKey1", "mKey2", "mKey3", "mKey4"), (id) -> Optional.of("Value2"))
                .collect(Collectors.toList()))
                .hasSize(4);
    }

    // Caches

    private CacheFuns<String, String> cache(final String name) {
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

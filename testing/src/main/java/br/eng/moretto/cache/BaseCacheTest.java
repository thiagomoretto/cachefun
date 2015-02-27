package br.eng.moretto.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import br.eng.moretto.cache.ops.CacheOperations;

abstract public class BaseCacheTest {
    private Cache cache;
    private final Random random = new Random();
    private String prefix;
    private final int samples = 5_000;

    @Before
    public void init() {
        cache = new J8Cache(getCacheOperationsProvider());
        prefix = String.valueOf(random.nextInt());
    }

    @Test
    public void testGet() throws Exception {
        cache.put(d("BasicGet:Item1"), new Item("BasicGet:Item1"));
        final Value<Item> value = cache.get(d("BasicGet:Item1"), Item.class);

        assertNotNull(value);
        assertTrue(value.isPresent());
        assertEquals("BasicGet:Item1", value.getValue().getName());
    }

    @Test
    public void testGetAll() throws Exception {
        cache.put(d("BasicGetAll:Item1"), new Item("BasicGetAll:Item1"));
        cache.put(d("BasicGetAll:Item2"), new Item("BasicGetAll:Item2"));

        final Values<Item> values = cache.getAll(
                    Arrays.asList(d("BasicGetAll:Item1"), d("BasicGetAll:Item2"),
                                        d("BasicGetAll:ItemNonExistent")), Item.class);

        assertEquals(3, values.size());
        assertEquals(2, values.stream().filter(v -> v.isPresent()).count());
        assertEquals(new Item("BasicGetAll:Item1"), values.mapOfPresentValues().get(d("BasicGetAll:Item1")));
        assertEquals(new Item("BasicGetAll:Item2"), values.mapOfPresentValues().get(d("BasicGetAll:Item2")));
    }

    @Test
    public void testGetAllWithFetchMany() throws Exception {
        cache.put(d("FetchMany:Item1"), new Item("Item1"));
        cache.put(d("FetchMany:Item2"), new Item("Item2"));

        final FetchMany<Item> fetcher = (ids, collector) -> {
            collector.collect(d("FetchMany:Item3"), new Item("Item3"));
        };

        assertEquals(3,
            cache.getAll(Arrays.asList(d("FetchMany:Item1"), d("FetchMany:Item2"), d("FetchMany:Item3")), Item.class, fetcher)
                .stream()
                .filter(v -> v.isPresent())
                .count());
    }

    @Test
    public void testGetAllWithFetchOne() throws Exception {
        cache.put(d("FetchOne:Item1"), new Item("Item1"));
        cache.put(d("FetchOne:Item2"), new Item("Item2"));

        final FetchOne<Item> fetchOne = (id) -> {
            if(d("FetchOne:Item3").equals(String.valueOf(id))) {
                return new Value<Item>(id, new Item("Item3"));
            } else {
                return new Value<Item>(id); // Missing object.
            }
        };

        final Collection<Object> ids = Arrays.asList(
                                    "FetchOne:Item1", "FetchOne:Item2", "FetchOne:Item3", "FetchOne:Item4")
                                    .stream().map(k -> new StringBuilder(prefix).append(k))
                                    .collect(Collectors.toList());

        assertEquals(3,
                cache.getAll(ids, Item.class, fetchOne)
                    .stream()
                    .filter(v -> v.isPresent())
                    .count());
    }

    @Test
    public void testGetAllWithFetchN() throws Exception {
        runGetAllBench("warmup", samples);
        runGetAllBench("real deal", samples);
    }

    private void runGetAllBench(final String name, final int nSamples) {
        final Collection<Object> ids = new ArrayList<>();
        for(int sample = 0;sample < nSamples; sample++) {
            String id;
            ids.add(id = d("FetchN:Item" + sample));
            cache.put(id, new Item("Item" + sample));
        }

        final long initial = System.nanoTime();

        final Values<Item> values = cache.getAll(ids, Item.class);

        final long ns = System.nanoTime() - initial;
        final long ms = ns/1_000_000;

        assertEquals(nSamples,
                values
                    .stream()
                    .filter(v -> v.isPresent())
                    .count());

        System.out.println("#runGetAllBench (samples=" + nSamples + "): '" + name+ "' = " + ns + "ns / " + ms + "ms");
        System.out.println("#runGetAllBench " + (nSamples / (ms/100.0)) + " op/s");
    }

    abstract protected CacheOperations getCacheOperationsProvider();

    // Utils

    public String d(final String key) {
        return new StringBuilder(prefix).append(key).toString();
    }

    // Fixtures

    static class Item {
        String name;

        public Item() {
            super();
        }

        public Item(final String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Item other = (Item) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Item [name=");
            builder.append(name);
            builder.append("]");
            return builder.toString();
        }
    }
}

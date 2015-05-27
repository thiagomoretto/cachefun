package br.eng.moretto.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import br.eng.moretto.cache.mappers.BoonCollectionMapper;
import br.eng.moretto.cache.mappers.BoonMapper;
import br.eng.moretto.cache.mappers.ContainerizedType;
import br.eng.moretto.cache.ops.CacheOperations;

abstract public class BaseCacheTest {
    private Cache<String, Item> cache;
    private Cache<String, Collection<Item>> cacheItems;
    private final CacheOperations cacheOperations = getCacheOperationsProvider();
    private final Random random = new Random();
    private String prefix;
    private final int samples = 5_000;

    @Before
    public void init() {
        cache = new CacheBuilder<String, Item>()
            .setCacheOperations(cacheOperations)
            .setKeyMapper(new StringKeyMapper())
            .setValueMapper(new BoonMapper<Item>(Item.class))
            .build();

        final Mapper<Collection<Item>> listMapper
            = new BoonCollectionMapper<Item, Collection<Item>>(ContainerizedType.listOfClass(Item.class));

        cacheItems = new CacheBuilder<String, Collection<Item>>()
                .setCacheOperations(cacheOperations)
                .setKeyMapper(new StringKeyMapper())
                .setValueMapper(listMapper)
                .build();

        prefix = String.valueOf(random.nextInt());
    }

    @Test
    public void testGet() throws Exception {
        cache.put(d("BasicGet:Item1"), new Item("BasicGet:Item1"));
        final Value<String, Item> value = cache.get(d("BasicGet:Item1"));

        assertNotNull(value);
        assertTrue(value.isPresent());
        assertEquals("BasicGet:Item1", value.getValue().getName());
    }

    @Test
    public void testGetCollection() throws Exception {
        final Item item1 = new Item("BasicGetCollection:SubItem1");
        final Item item2 = new Item("BasicGetCollection:SubItem2");
        final List<Item> items = Arrays.asList(item1, item2);

        cacheItems.put(d("BasicGetCollection:Item1"), items);

        final Value<String, Collection<Item>> value = cacheItems.get(d("BasicGetCollection:Item1"));

        assertNotNull(value);
        assertTrue(value.isPresent());

        final List<Item> loadedItems = (List<Item>) value.getValue();
        assertEquals(2, loadedItems.size());
        assertEquals(item1, loadedItems.get(0));
        assertEquals(item2, loadedItems.get(1));
    }

    @Test
    public void testGetWithFetchOne() throws Exception {
        cache.put(d("BasicGetFetchOne:Item1"), new Item("BasicGetFetchOne:Item1"));
        final Value<String, Item> value = cache.get(d("BasicGetFetchOne:Item1"),
                v -> v.collect(new Item("BasicGetFetchOne:Item1")));

        assertNotNull(value);
        assertTrue(value.isPresent());
        assertEquals("BasicGetFetchOne:Item1", value.getValue().getName());

        // just check again.
        assertNotNull(cache.get(d("BasicGetFetchOne:Item1")));
    }

    @Test
    public void testGetAll() throws Exception {
        cache.put(d("BasicGetAll:Item1"), new Item("BasicGetAll:Item1"));
        cache.put(d("BasicGetAll:Item2"), new Item("BasicGetAll:Item2"));

        final Collection<String> keys = Arrays.asList( //
                d("BasicGetAll:Item1"), d("BasicGetAll:Item2"), d("BasicGetAll:ItemNonExistent"));
        final Values<String, Item> values = cache.getAll(keys);

        assertEquals(3, values.size());
        assertEquals(2, values.stream().filter(v -> v.isPresent()).count());
        assertEquals(new Item("BasicGetAll:Item1"), values.mapOfPresentValues().get(d("BasicGetAll:Item1")));
        assertEquals(new Item("BasicGetAll:Item2"), values.mapOfPresentValues().get(d("BasicGetAll:Item2")));
    }

    @Test
    public void testGetAllWithFetchMany() throws Exception {
        cache.put(d("FetchMany:Item1"), new Item("Item1"));
        cache.put(d("FetchMany:Item2"), new Item("Item2"));

        final FetchMany<String, Item> fetcher = (values) -> {
            values.collect(d("FetchMany:Item3"), new Item("Item3"));
        };

        assertEquals(3, //
                cache.getAll(Arrays.asList(d("FetchMany:Item1"), d("FetchMany:Item2"), d("FetchMany:Item3")), fetcher) //
                        .stream() //
                        .filter(v -> v.isPresent()) //
                        .count());
    }

    @Test
    public void testGetAllWithFetchOne() throws Exception {
        cache.put(d("FetchOne:Item1"), new Item("Item1"));
        cache.put(d("FetchOne:Item2"), new Item("Item2"));

        final FetchOne<String, Item> fetchOne = (v) -> {
            if (d("FetchOne:Item3").equals(String.valueOf(v.getKey()))) {
                v.collect(new Item("Item3"));
            } else {
                v.collect(null); // Missing object, or just no call this method.
            }
        };

        final Collection<String> ids //
        = Arrays.asList("FetchOne:Item1", "FetchOne:Item2", "FetchOne:Item3", "FetchOne:Item4") //
                .stream() //
                .map(k -> new StringBuilder(prefix).append(k).toString()) //
                .collect(Collectors.toList());

        assertEquals(3, //
                cache.getAll(ids, fetchOne) //
                        .stream() //
                        .filter(v -> v.isPresent()) //
                        .count());
    }

    @Test
    public void testGetAllWithFetchN() throws Exception {
        runGetAllBench("warmup", samples);
        runGetAllBench("real deal", samples);
    }

    private void runGetAllBench(final String name, final int nSamples) {
        final Collection<String> ids = new ArrayList<>();
        for (int sample = 0; sample < nSamples; sample++) {
            String id;
            ids.add(id = d("FetchN:Item" + sample));
            cache.put(id, new Item("Item" + sample));
        }

        final long initial = System.nanoTime();

        final Values<String, Item> values = cache.getAll(ids);

        final long ns = System.nanoTime() - initial;
        final long ms = ns / 1_000_000;

        assertEquals(nSamples, //
                values //
                .stream() //
                        .filter(v -> v.isPresent()) //
                        .count());

        System.out.println("#runGetAllBench (samples=" + nSamples + "): '" + name + "' = " + ns + "ns / " + ms + "ms");
        System.out.println("#runGetAllBench " + nSamples / (ms / 100.0) + " op/s");
    }

    abstract protected CacheOperations getCacheOperationsProvider();

    // Utils

    public String d(final String key) {
        return new StringBuilder(prefix).append(key).toString();
    }

    // Fixtures

    static class Item {
        private String name;

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
            result = prime * result + (name == null ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Item other = (Item) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
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

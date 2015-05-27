# j8cache


![](https://travis-ci.org/thiagomoretto/j8cache.svg)

A early stage cache library for Java 8 applications, created to deliver a delightful programming interface, performance, focusing in bulk operations to maximize throughput and without any pretensions to implement JSR107.

This is being done from scratch, made from the experience of previously successful cache libraries, with the similar approach, that is running well and delivering cached data to one of the biggest LATAM e-commerce site, but targeted to Java 7 and not open-sourced.

Currently we have just the Redis as cache storage backend, but another one can be added by implementing a small set of basic operations.

Since 2015.

## Examples

Initialize it:

```java
CacheFuns<String, String> cache = new CacheFuns<String, String>(
        new RedisCacheOperations(new JedisPool("localhost")),
                /* KeyMapper */,
                /* ValueMapper */);
```

Where `CacheFuns` is defined as this: `CacheFuns<K, T>` where `K` is the type of the key, and `T` is the type of the stored object.

So, with the cache initialized, you can do a simple get:

```java
Optional<String> value = cache.get("key1");
```

But you can also pass a `Supplier<T>` as parameters to supply the value when it is missing in the cache, and  automatically the value loaded from our data source is stored, as this example demonstrates:

```java
Optional<String> value = cache.get("key1", (id) -> Optional.of("Value1"))
value.get(); // returns Value1
cache.get("key1").isPresent(); // returns true.
```

Or you can get a stream of stored values using `streamOf` methods:

```java
cache.streamOf(Arrays.asList("Key1", "Key2", "Key3"))
     .filter(...)
     .forEach(...)
```

Or even you can use the `supplyStreamOf` to get a `Supplier` of an `Stream`:

```java
Supplier<Stream<Optional<T>>> supplierOfCustomers = cache.supplyStreamOf(Arrays.asList("Customer1", "Customer2")
...
supplierOfCustomers.get(); // Finally executes the operation.
```

It also has a `streamOf` method that accepts a function which you can provide the real values for missing keys:

```java
Collection<String> customerIds = Arrays.asList("Customer1", "Customer2", "CustomerN")

Stream<Optional<String>> customersStream = main.streamOf(ids, (id) -> {
  /* 
  Loads the customer from database based on given 'id'
  */
})
```

Using `Redis` as backend, all batch operations (such as `getAll`, `streamOf` and `supplyStreamOf`) are done pipelined to maximize performance.

Being 'functional' give us more power to compose operations, as an example, you can create L1 L2 cache operations easily just by composing these functions. Let's take a look at these examples, given two caches, one of them `l1`  is a fast in-memory cache, and a `l2`, a Redis acting as cache, slower than L1 of course.

And the `get` is defined as this:  `Supplier<Optional<T>> get(final K id, final Supplier<Optional<T>> supplier)`, you can do a L1 L2 get:

```java
Optional<String> value = //
        l1.get(key, //
        l2.get(key, () -> Optional.of(/* something loading from database by example */))).get();
```

It tries to load from `l1`, after `l2` and finally hits the database if the caches didn't found the stored value represented by its key. You can compose `N` caches, but we don't know why you need this, but who knows.

For further details and examples, take a look at the unit tests. We are working hard on this library, and very soon you gonna see some new features and support to another backends.

## License

The MIT License (MIT)

Copyright (c) 2015 j8cache

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

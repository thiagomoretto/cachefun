package br.eng.moretto.cache;


public interface Mapper<T> {

    public String write(T object);

    public T read(String json);

}

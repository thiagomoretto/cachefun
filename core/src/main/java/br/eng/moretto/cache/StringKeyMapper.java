package br.eng.moretto.cache;

public class StringKeyMapper implements Mapper<String> {

    @Override
    public String write(final String object) {
        return object;
    }

    @Override
    public String read(final String object) {
        return object;
    }
}

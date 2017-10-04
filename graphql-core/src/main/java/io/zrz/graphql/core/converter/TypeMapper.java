package io.zrz.graphql.core.converter;

public interface TypeMapper<I, O>
{

  O convert(I input);

}

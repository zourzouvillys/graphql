package io.joss.graphql.core.converter;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.time.Instant;

import org.junit.Test;

public class TypeConverterTest
{

  @Test
  public void test() throws Exception
  {
    
    assertEquals(1234, TypeConverter.defaultConverter().convert("1234", Integer.class));
    assertEquals((Double)1.2, TypeConverter.defaultConverter().convert("1.2", Double.class));
    assertEquals(new Float(1234), TypeConverter.defaultConverter().convert("1234", Float.class));
    assertEquals(new BigInteger("12341234123412341234123412341234123412341234"), TypeConverter.defaultConverter().convert("12341234123412341234123412341234123412341234", BigInteger.class));
    
    Instant ts = Instant.now();
    assertEquals(ts, TypeConverter.defaultConverter().convert(ts.toString(), Instant.class));
    
    
  }

}

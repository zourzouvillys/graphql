package io.joss.graphql.core.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

import org.junit.Test;

import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValueTypeConverter;
import io.joss.graphql.core.value.GQLValues;
import lombok.Value;

public class TypeConverterTest
{

  @Test
  public void test() throws Exception
  {

    assertEquals(1234, TypeConverter.defaultConverter().convert("1234", Integer.class));
    assertEquals((Double) 1.2, TypeConverter.defaultConverter().convert("1.2", Double.class));
    assertEquals(new Float(1234), TypeConverter.defaultConverter().convert("1234", Float.class));
    assertEquals(new BigInteger("12341234123412341234123412341234123412341234"),
        TypeConverter.defaultConverter().convert("12341234123412341234123412341234123412341234", BigInteger.class));

    Instant ts = Instant.now();
    assertEquals(ts, TypeConverter.defaultConverter().convert(ts.toString(), Instant.class));

  }

  @Test
  public void testDefaultValues() throws Exception
  {

    // Integer.TYPE is the primitive one, so returns a default value (0).
    Integer val = TypeConverter.defaultConverter().convert(null, Integer.TYPE);
    assertNotNull(val);
    assertEquals(0, (int) val);

    assertNull(TypeConverter.defaultConverter().convert(null, Integer.class));

  }

  @Value
  public static class TestClass
  {
    public String key;
    private List<Integer> vals;
  }

  @Test
  public void testTypeMaterializers() throws Exception
  {
    GQLObjectValue input = GQLObjectValue.builder()
        .value("key", GQLValues.stringValue("xxx"))
        .value("vals", GQLValues.listValue(GQLValues.stringValue("1"), GQLValues.stringValue("2")))
        .build();
    TestClass val = new GQLValueTypeConverter().convert(input, TestClass.class);
    assertEquals("xxx", val.key);
    assertEquals(2, val.vals.size());
    assertEquals(1, (int) val.vals.get(0));
    assertEquals(2, (int) val.vals.get(1));
  }

}

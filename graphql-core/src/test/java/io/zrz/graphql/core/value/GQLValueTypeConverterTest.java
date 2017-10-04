package io.zrz.graphql.core.value;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.zrz.graphql.core.value.GQLValueTypeConverter;
import io.zrz.graphql.core.value.GQLValues;

public class GQLValueTypeConverterTest
{

  @Test
  public void test()
  {
    
    GQLValueTypeConverter c = new GQLValueTypeConverter();
    
    assertEquals("xxx", c.convert(GQLValues.stringValue("xxx"), String.class));
    assertEquals(1, (int)c.convert(GQLValues.stringValue("1"), Integer.class));
    
  }

}

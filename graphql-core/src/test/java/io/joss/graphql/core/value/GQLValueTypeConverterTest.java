package io.joss.graphql.core.value;

import static org.junit.Assert.*;

import org.junit.Test;

public class GQLValueTypeConverterTest
{

  @Test
  public void test()
  {
    
    GQLValueTypeConverter c = new GQLValueTypeConverter();
    
    assertEquals("xxx", c.convert(GQLValues.stringValue("xxx"), String.class));
    assertEquals(1, c.convert(GQLValues.stringValue("1"), Integer.class));
    assertEquals(1, c.convert(GQLValues.stringValue("1"), Integer.TYPE));
    
  }

}

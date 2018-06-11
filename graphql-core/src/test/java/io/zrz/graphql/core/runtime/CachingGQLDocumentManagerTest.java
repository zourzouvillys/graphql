package io.zrz.graphql.core.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CachingGQLDocumentManagerTest {

  @Test
  public void test() {

    CachingGQLDocumentManager mgr = new CachingGQLDocumentManager();

    GQLPreparedDocument ops = mgr.prepareDocument("query XXX { xxx }");

    assertEquals(1, ops.operations().count());

  }

}

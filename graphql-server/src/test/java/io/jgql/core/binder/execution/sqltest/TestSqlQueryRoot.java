package io.jgql.core.binder.execution.sqltest;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType
public class TestSqlQueryRoot
{

  private String domain;

  public TestSqlQueryRoot(String domain)
  {
    this.domain = domain;
  }

  @GQLField
  public Tenant getTenant()
  {
    return new Tenant(domain);
  }

}

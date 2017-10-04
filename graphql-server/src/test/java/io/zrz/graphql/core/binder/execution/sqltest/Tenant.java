package io.zrz.graphql.core.binder.execution.sqltest;

import java.util.List;

import com.google.common.collect.Lists;

import io.zrz.graphql.core.binder.annotatons.GQLContext;
import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLType;
import io.zrz.graphql.core.binder.runtime.DataContext;

@GQLType
public class Tenant
{

  private String domain;

  public Tenant(String domain)
  {
    this.domain = domain;
  }

  @GQLField
  public List<Employee> getEmployees(@GQLContext DataContext ctx)
  {
    return Lists.newArrayList(
        new Employee("theo", 1234),
        new Employee("mike", 1234),
        new Employee("john", 0)
        );
  }

}

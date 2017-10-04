package io.zrz.graphql.core.binder.execution.sqltest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;

import io.zrz.graphql.core.binder.annotatons.GQLArg;
import io.zrz.graphql.core.binder.annotatons.GQLContext;
import io.zrz.graphql.core.binder.annotatons.GQLDefaultValue;
import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLType;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.binder.runtime.InputObserver;
import io.zrz.graphql.core.binder.runtime.OutputObserver;

@GQLType
public class Employee
{

  private int managerId;
  private String name;

  public Employee(String name, int managerId)
  {
    this.name = name;
    this.managerId = managerId;
  }

  @GQLField
  public String getId()
  {
    return Integer.toHexString(name.hashCode());
  }

  @GQLField
  public String getName()
  {
    return name;
  }

  @GQLField
  public int getAge(@GQLArg("age") @GQLDefaultValue("33") int age)
  {
    return age;
  }

  /**
   * In this more complicated example, we generate a single SQL SELECT for all branches. We only select the fields which are actually being
   * queried.
   */

  @GQLField
  public static InputObserver<Employee, Employee> getManager(@GQLContext DataContext ctx)
  {

    Set<Integer> selections = new HashSet<>();

    return new InputObserver<Employee, Employee>() {

      private Map<Employee, OutputObserver<Employee, Employee>> pending = new HashMap<>();

      @Override
      public void onParent(Employee parent, OutputObserver<Employee, Employee> output)
      {
        selections.add(parent.managerId);
        pending.put(parent, output);
      }

      @Override
      public void onCompleted()
      {

        List<String> fields = new LinkedList<>();

        for (DataContext child : ctx.children())
        {
          fields.add(child.name());
        }

        System.err.println("SELECT " + Joiner.on(", ").join(fields) + " FROM employees WHERE id IN (" + Joiner.on(", ").join(selections) + ")");

        for (Map.Entry<Employee, OutputObserver<Employee, Employee>> e : pending.entrySet())
        {
          e.getValue().onNext(e.getKey(), new Employee("Mr. Manager: " + Integer.toString(e.getKey().managerId), 666));
          e.getValue().onComplete();
        }

      }

    };

  }

}

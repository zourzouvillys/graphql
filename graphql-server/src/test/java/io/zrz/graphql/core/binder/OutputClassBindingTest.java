
package io.zrz.graphql.core.binder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.zrz.graphql.core.binder.annotatons.GQLArg;
import io.zrz.graphql.core.binder.annotatons.GQLContext;
import io.zrz.graphql.core.binder.annotatons.GQLType;
import io.zrz.graphql.core.binder.model.OutputClassBinding;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.binder.runtime.InputObserver;
import io.zrz.graphql.core.binder.runtime.OutputObserver;
import io.zrz.graphql.core.binder.testmodel.SomeNonInnerClass;

public class OutputClassBindingTest
{

  @GQLType(autoField = true)
  public static final class MyClass
  {

    public int getId()
    {
      return 0;
    }

    public static InputObserver<SomeNonInnerClass, MyClass> getAsyncField(@GQLContext DataContext selector)
    {

      return new InputObserver<SomeNonInnerClass, MyClass>() {

        @Override
        public void onParent(MyClass parent, OutputObserver<MyClass, SomeNonInnerClass> output)
        {
          output.onNext(parent, new SomeNonInnerClass("A"));
          output.onNext(parent, new SomeNonInnerClass("B"));
          output.onNext(parent, new SomeNonInnerClass("C"));
          output.onComplete();
        }

        @Override
        public void onCompleted()
        {
          // TODO Auto-generated method stub
        }

      };

    }

    public String getSize(@GQLArg("type") String type, @GQLContext DataContext ctx)
    {
      return "hello";
    }

  }

  @Test
  public void test()
  {

    OutputClassBinding binding = OutputClassBinding.bind(MyClass.class);

    assertEquals(3, binding.fields().size());

    // some type

    assertEquals(Integer.TYPE, binding.fields("id").get(0).returnType().rawClass());
    assertEquals(0, binding.fields("id").get(0).contextParams().size());
    assertEquals(0, binding.fields("id").get(0).inputParams().size());

    // test the wrapped return type.

    assertEquals(SomeNonInnerClass.class, binding.fields("asyncField").get(0).returnType().rawClass());

    assertEquals(0, binding.fields("asyncField").get(0).inputParams().size());
    assertEquals(1, binding.fields("asyncField").get(0).contextParams().size());

    // test return types etc.

    assertEquals(String.class, binding.fields("size").get(0).returnType().rawClass());
    assertEquals(1, binding.fields("size").get(0).contextParams().size());
    assertEquals(1, binding.fields("size").get(0).inputParams().size());

    // ///
    //
    // FieldInvoker val = binding.fields("size").get(0).invoker(
    // new DataContext(new GQLTypeRegistry(Collections.emptyMap()), GQLTypes.typeRef("xxx")),
    // QueryEnvironment.builder().build());
    //
    // //
    // FieldInvoker input = binding.fields("asyncField").get(0).invoker(null, null);
    //
    // Assert.assertNotNull(input);

  }

}

package io.zrz.graphql.zulu.schema;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.zulu.JavaInputField;
import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.Ref;
import io.zrz.graphql.zulu.User;
import io.zrz.graphql.zulu.annotations.GQLDocumentation;
import io.zrz.graphql.zulu.annotations.GQLOutputExtension;
import io.zrz.graphql.zulu.annotations.GQLTypeUse;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder;
import io.zrz.graphql.zulu.spi.ExtensionGenerator;

@SuppressWarnings("serial")
public class SchemaGeneratorTest {

  public static class TestA implements TestUnregisteredInterface {

  }

  public static class TestB implements TestUnregisteredInterface {

  }

  public static interface TestUnregisteredInterface {

  }

  /**
   * 
   */

  public static class TestItem {

    @GQLTypeUse(name = "SomethingResultUnion")
    public TestUnregisteredInterface something() {
      return new TestA();
    }

  }

  /**
   * 
   */

  @GQLOutputExtension
  public static class TestExtension {

    @GQLDocumentation("generated by a static extension")
    public static String someExtension(Ref<User> user, String value) {
      return "xyz";
    }

  }

  public static class QueryRoot {

    public User user(String username) {
      return null;
    }

    public User team() {
      return null;
    }

    public String hello() {
      return "hey there";
    }

  }

  public static class MutationRoot {

  }

  /**
   * 
   */

  @Test
  public void test() {

    ExecutableSchemaBuilder j = new ExecutableSchemaBuilder();

    // j.setRootType(GQLOpType.Query, QueryRoot.class);
    j.setRootType(GQLOpType.Mutation, MutationRoot.class);

    // j.addType(User.class);
    // j.addType(Team.class);
    // j.addType(TestItem.class);
    // j.addType(TestB.class);
    // j.addType(TestA.class);

    // j.addUnion(TestUnregisteredInterface.class);

    // j.addScalar(new TypeToken<Ref<User>>() {}, "ID");
    // j.addScalar(new TypeToken<RefSet<Team>>() {}, "TeamConnection");

    // j.registerExtension(TestExtension.class);

    j.extensionGenerator(new ExtensionGenerator() {

      @Override
      public Stream<JavaOutputField> generateExtensions(TypeToken<?> type) {

        if (!type.equals(TypeToken.of(MutationRoot.class))) {
          return Stream.empty();
        }

        return Stream.of(new JavaOutputField() {

          @Override
          public TypeToken<?> returnType() {
            // TODO Auto-generated method stub
            return TypeToken.of(String.class);
          }

          @Override
          public <T, C, V> T invoke(V request, C context, Object... args) {
            // TODO Auto-generated method stub
            return (T) "boo";
          }

          @Override
          public String fieldName() {
            // TODO Auto-generated method stub
            return "xyz";
          }
        });
      }

    });

    ExecutableSchema schema = j.build();

    //
    System.err.println(new SchemaGenerator(schema).generate());

    //

    String res = schema.rootType(GQLOpType.Query)
        .get()
        .field("hello")
        .get()
        .invoke(null, new QueryRoot());

    assertEquals("hey there", res);

    assertEquals("boo",
        schema.rootType(GQLOpType.Mutation)
            .get()
            .field("xyz")
            .get()
            .invoke(null, new MutationRoot())

    );

  }

  @GQLOutputExtension
  public static class DynamicExtension implements ExtensionGenerator {

    @Override
    public Stream<JavaOutputField> generateExtensions(TypeToken<?> view) {

      if (view.equals(TypeToken.of(User.class))) {

        //
        return Stream.of(new JavaOutputField() {

          @Override
          public String fieldName() {
            return "test";
          }

          @Override
          public Stream<? extends JavaInputField> inputFields() {
            return Stream.of(new JavaInputField() {

              @Override
              public String fieldName() {
                return "id";
              }

              @Override
              public TypeToken<?> inputType() {
                return TypeToken.of(String.class);
              }

              @Override
              public int index() {
                return 0;
              }

              @Override
              public <T extends Annotation> Optional<T> annotation(Class<T> klass) {
                return Optional.empty();
              }

            });
          }

          @Override
          public TypeToken<?> returnType() {
            // TODO Auto-generated method stub
            return TypeToken.of(String.class);
          }

          @Override
          public String documentation() {
            return "example dynamically generated field";
          }

          @Override
          public <T, C, V> T invoke(V request, C context, Object... args) {
            throw new RuntimeException("not implemented");
          }

        });

      }

      return Stream.empty();
    }

  }

}

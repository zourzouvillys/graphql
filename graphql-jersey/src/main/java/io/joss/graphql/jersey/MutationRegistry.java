package io.joss.graphql.jersey;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.joss.graphql.core.binder.TypeScanner;
import io.joss.graphql.core.binder.annotatons.GQLContext;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.core.binder.model.InputClassBinding;
import io.joss.graphql.core.decl.GQLArgumentDefinition;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.joss.graphql.core.types.GQLNonNullType;
import io.joss.graphql.core.types.GQLTypes;
import io.joss.graphql.jersey.mutations.Mutation;

/**
 * Registry/dictionary of available mutations.
 * 
 * Different dictionary instances can be returned for different users, based on their permissions etc.
 * 
 * @author theo
 *
 */

public class MutationRegistry
{

  private List<MutationHandler> handlers = new LinkedList<>();

  /**
   * scan for @Mutation methods.
   * 
   * The methods should take @GQLContext parameters and a single value based @GQLType object named $input.
   * 
   * 
   * @param instance
   */

  public void register(Object instance)
  {

    Class<?> klass = instance.getClass();

    for (Method method : klass.getDeclaredMethods())
    {

      MutationHandler.Builder mhb = MutationHandler.builder();

      Mutation m = method.getAnnotation(Mutation.class);

      if (m == null)
      {
        continue;
      }

      Class<?> returnType = method.getReturnType();

      if (!returnType.isAnnotationPresent(GQLType.class))
      {
        throw new IllegalArgumentException(String.format("%s must be annotated with @GQLType", method.getReturnType()));
      }

      Integer input = null;
      // work out all the parameters.

      mhb.name(method.getName());

      mhb.returnType(returnType);

      for (int i = 0; i < method.getParameterCount(); ++i)
      {

        Annotation[] pats = method.getParameterAnnotations()[i];
        Class<?> ptype = method.getParameterTypes()[i];

        if (Arrays.stream(pats).filter(p -> p.equals(GQLContext.class)).findAny().isPresent())
        {
        }
        else if (input == null)
        {

          if (!ptype.isAnnotationPresent(GQLType.class))
          {
            throw new IllegalArgumentException("parameter on mutation must be @GQLType");
          }

          // allow just a single value.
          input = i;
          mhb.inputType(ptype);

        }
        else
        {
          throw new RuntimeException("Only @GQLContext and single @GQLType is allowed");
        }

      }

      handlers.add(mhb.build());

    }

  }

  public List<MutationHandler> handlers()
  {
    return handlers;
  }

  /**
   * applies the registered types to the scanner, and returns the {@link GQLObjectTypeDeclaration} that represents our root.
   */

  public GQLObjectTypeDeclaration build(TypeScanner scanner)
  {

    GQLObjectTypeDeclaration.Builder decl = GQLObjectTypeDeclaration.builder();

    decl.name("MutationRoot");

    for (MutationHandler handler : handlers())
    {

      GQLParameterableFieldDeclaration.Builder field = GQLParameterableFieldDeclaration.builder();

      field.name(handler.name());

      // we merge the returned object along with our clientMutationId value which we take care of.
      
      GQLObjectTypeDeclaration returnType = GQLObjectTypeDeclaration.builder()
          .name(String.format("%sPayload", capitalize(handler.name())))
          .field(GQLParameterableFieldDeclaration.builder().name("result").type(GQLTypes.nonNull(scanner.ref(handler.returnType()))).build())
          .field(GQLParameterableFieldDeclaration.builder().name("clientMutationId").type(GQLTypes.nonNullStringType()).build())
          .build();

      field.type(scanner.add(returnType));

      String commandName = scanner.calculateName(handler.inputType());

      GQLNonNullType inputType = GQLTypes.nonNull(GQLTypes.ref(scanner.add(InputClassBinding.bind(handler.inputType()), commandName)));

      field.arg(GQLArgumentDefinition.builder().name("$input").type(inputType).build());

      decl.field(field.build());

    }

    return decl.build();

  }

  private String capitalize(String name)
  {
    return StringUtils.capitalize(name);
  }

}
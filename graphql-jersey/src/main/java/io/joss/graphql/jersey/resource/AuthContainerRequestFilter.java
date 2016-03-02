package io.joss.graphql.jersey.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;

import io.joss.graphql.jersey.RegistryHttpUtils;
import io.joss.graphql.jersey.auth.RegistryAuthValue;
import io.joss.graphql.jersey.auth.RegistryBearerAuthValue;

/**
 * A simple (hah) binder which adds support for injecting {@link UnverifiedCredentials} when a RAX-RS method is annotatated with @AuthParam
 */

public class AuthContainerRequestFilter extends AbstractBinder
{

  @Override
  protected void configure()
  {

    bind(AuthHeaderParamValueFactoryProvider.class)
        .to(ValueFactoryProvider.class)
        .in(Singleton.class);

    bind(UserAuthHeaderParamResolver.class)
        .to(new TypeLiteral<InjectionResolver<AuthParam>>() {
        })
        .in(Singleton.class);

  }

  public static class UserAuthHeaderParamResolver extends ParamInjectionResolver<AuthParam>
  {
    public UserAuthHeaderParamResolver()
    {
      super(AuthHeaderParamValueFactoryProvider.class);
    }
  }

  public static class AuthHeaderParamValueFactoryProvider extends AbstractValueFactoryProvider
  {

    @Inject
    protected AuthHeaderParamValueFactoryProvider(MultivaluedParameterExtractorProvider mpep, ServiceLocator locator)
    {
      super(mpep, locator, Parameter.Source.UNKNOWN);
    }

    @Override
    protected Factory<?> createValueFactory(Parameter parameter)
    {

      Class<?> classType = parameter.getRawType();

      if (classType == null || (!classType.equals(UnverifiedCredentials.class)))
      {
        return null;
      }

      return new MyContextValueFactory();

    }

  }

  private static class MyContextValueFactory implements Factory<UnverifiedCredentials>
  {

    @Inject
    private Provider<ContainerRequest> request;

    @Override
    public UnverifiedCredentials provide()
    {
      return scan(request.get());
    }

    @Override
    public void dispose(UnverifiedCredentials instance)
    {
    }

  }

  /**
   * scan the request to find any matching credentials we want to provide to the method.
   */

  private static UnverifiedCredentials scan(ContainerRequestContext ctx)
  {

    List<RegistryAuthValue> values = Lists.newLinkedList();

    List<String> qp = ctx.getUriInfo().getQueryParameters().get("access_token");

    if (qp != null)
    {
      qp.forEach(value -> values.add(RegistryBearerAuthValue.fromToken(value)));
    }

    for (String value : ctx.getHeaders().entrySet().stream()
        .filter(p -> p.getKey().equals(HttpHeaders.AUTHORIZATION))
        .flatMap(p -> p.getValue().stream())
        .collect(Collectors.toSet()))
    {
      values.add(RegistryHttpUtils.parseAuth(value));
    }

    return new UnverifiedCredentials(values);

  }

}

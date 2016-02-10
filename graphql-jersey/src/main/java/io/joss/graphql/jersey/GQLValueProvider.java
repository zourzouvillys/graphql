package io.joss.graphql.jersey;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.joss.graphql.core.binder.BasicJsonGenerator;
import io.joss.graphql.core.binder.JsonValueWriter;
import io.joss.graphql.core.value.GQLValue;

@Produces(MediaType.APPLICATION_JSON)
public class GQLValueProvider implements MessageBodyWriter<GQLValue>
{

  @Context
  public UriInfo info;

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
  {
    return GQLValue.class.isAssignableFrom(type);
  }

  @Override
  public long getSize(GQLValue t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
  {
    return -1;
  }

  @Override
  public void writeTo(
      GQLValue t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream) throws IOException, WebApplicationException
  {
    OutputStreamWriter strm = new OutputStreamWriter(entityStream);
    BasicJsonGenerator gen = new JacksonJsonGenerator(mapper, strm, info.getQueryParameters().containsKey("pretty"));
    t.apply(new JsonValueWriter(gen));
    gen.flush();
  }

}
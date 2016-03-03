package io.joss.graphql.jersey.resource;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;

import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLOpType;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.parser.GQLParser;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLValues;
import io.joss.graphql.jersey.GraphQLEngineCore;
import io.joss.graphql.jersey.RegistryHttpUtils;
import io.joss.graphql.jersey.RequestContext;
import io.joss.graphql.jersey.RequestType;
import io.joss.graphql.jersey.auth.RegistryAuthValue;
import io.joss.graphql.jersey.auth.RegistryBearerAuthValue;
import io.joss.graphql.jersey.http.HttpErrorMessage;
import lombok.Data;
import lombok.SneakyThrows;

@Path("/graphql")
public class GraphQLResource
{

  private static final MediaType GQL_SCHEMA_TYPE = new MediaType("application", "x-graphql-schema");
  private GraphQLEngineCore core;

  public GraphQLResource(GraphQLEngineCore core)
  {
    this.core = core;
  }

  @GET
  @Path("schema")
  public Response scheme()
  {
    return Response.status(200).entity(core.schema()).type(GQL_SCHEMA_TYPE).build();
  }

  @GET
  @Produces("application/x-graphql-schema")
  public Response schema()
  {
    return Response.status(200).entity(core.schema()).type(GQL_SCHEMA_TYPE).build();
  }

  @GET
  @JacksonFeatures(serializationEnable = { SerializationFeature.INDENT_OUTPUT })
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(
      @QueryParam("schema") Boolean schema,
      @HeaderParam(HttpHeaders.AUTHORIZATION) String auth,
      @QueryParam("auth_token") String queryAuthToken,
      @QueryParam("q") String querystr,
      @QueryParam("op") String opname)
  {

    if (schema != null)
    {
      return schema();
    }

    if (querystr == null || querystr.trim().isEmpty())
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.MISSING_PARAMETER, "query required")).build();
    }

    RegistryAuthValue authValue = RegistryHttpUtils.parseAuth(auth);

    if (authValue == null)
    {
      authValue = RegistryBearerAuthValue.fromToken(queryAuthToken);
    }

    GQLDocument doc = GQLParser.parseDocument(getQuery(querystr));

    final GQLSelectedOperation query;

    try
    {
      query = GQLSelectedOperation.namedQuery(doc, opname);
    }
    catch (IllegalArgumentException ex)
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.INVALID_OPERATION, String.format("Invalid operation '%s'", opname))).build();
    }

    if (query.operation().type() != GQLOpType.Query)
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.INVALID_OP_TYPE, "only query is alllwed over HTTP GET")).build();
    }

    try (RequestContext ctx = core.open(RequestType.QUERY, authValue))
    {
      return Response.status(200).entity(ctx.query(query, GQLValues.objectValue())).build();
    }

  }

  @SneakyThrows
  private String getQuery(String query)
  {
    return query;
  }

  @Data
  public static class HttpGraphQLQueryData
  {
    String query;
    String access_token;
    JsonNode variables;
    String operation;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonFeatures(serializationEnable = { SerializationFeature.INDENT_OUTPUT })
  public Response post(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("auth_token") String queryAuthToken, HttpGraphQLQueryData body)
  {

    RegistryAuthValue authValue = RegistryHttpUtils.parseAuth(auth);

    if (body == null)
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.MISSING_PARAMETER, "body required")).build();
    }

    if (authValue == null)
    {
      authValue = RegistryBearerAuthValue.fromToken(queryAuthToken);
    }
    else if (body.access_token != null)
    {
      authValue = RegistryBearerAuthValue.fromToken(body.access_token);
    }

    GQLDocument doc = GQLParser.parseDocument(getQuery(body.query));

    final GQLSelectedOperation query;

    GQLValue input = (GQLObjectValue) convertToGQL(body.variables);

    try
    {
      query = GQLSelectedOperation.namedQuery(doc, body.operation);
    }
    catch (IllegalArgumentException ex)
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.INVALID_OPERATION, String.format("Invalid operation '%s'", body.operation))).build();
    }

    try (RequestContext ctx = core.open(RequestType.MUTATION, authValue))
    {
      Stopwatch timer = Stopwatch.createStarted();
      ResponseBuilder rb = Response.status(200);
      try
      {
        rb.entity(ctx.query(query, input));
      }
      finally
      {
        rb.header("X-JGQL-Timer", timer.stop().elapsed(TimeUnit.MICROSECONDS));
      }
      return rb.build();
    }

  }

  private GQLValue convertToGQL(JsonNode value)
  {

    if (value == null)
    {
      return null;
    }

    switch (value.getNodeType())
    {
      case ARRAY:
        List<GQLValue> values = Lists.newArrayList();
        for (JsonNode child : value)
        {
          values.add(convertToGQL(child));
        }
        return GQLValues.listValue(values);
      case BINARY:
        throw new RuntimeException("Not supported");
      case BOOLEAN:
        return GQLValues.booleanValue(value.asBoolean());
      case NUMBER:
        return GQLValues.floatValue(value.asDouble());
      case OBJECT:
        Iterator<Map.Entry<String, JsonNode>> it = ((ObjectNode) value).fields();
        Map<String, GQLValue> map = Maps.newLinkedHashMap();
        while (it.hasNext())
        {
          Entry<String, JsonNode> e = it.next();
          map.put(e.getKey(), convertToGQL(e.getValue()));
        }
        return GQLValues.objectValue(map);
      case STRING:
        return GQLValues.stringValue(value.asText());
      case NULL:
      case MISSING:
      case POJO:
      default:
        break;
    }

    return GQLObjectValue.builder().value("in", GQLValues.booleanFalse()).build();
  }

}
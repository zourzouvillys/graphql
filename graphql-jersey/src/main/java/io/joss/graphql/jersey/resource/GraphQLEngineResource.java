package io.joss.graphql.jersey.resource;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import io.joss.graphql.jersey.RegistryHttpUtils;
import io.joss.graphql.jersey.auth.RegistryAuthValue;
import io.joss.graphql.jersey.auth.RegistryBearerAuthValue;
import io.joss.graphql.jersey.http.HttpErrorMessage;
import lombok.Data;
import lombok.SneakyThrows;

@Path("/graphql")
public class GraphQLEngineResource
{

  private static final MediaType GQL_SCHEMA_TYPE = new MediaType("application", "x-graphql-schema");
  private GraphQLRestRootProvider root;

  public GraphQLEngineResource(GraphQLRestRootProvider root)
  {
    this.root = root;
  }

  @OPTIONS
  public Response options(@HeaderParam("Access-Control-Request-Headers") String corsHeaders)
  {
    return Response.status(202)
        .header("Access-Control-Allow-Origin", "*")
        .header("Access-Control-Allow-Credentials", "true")
        .header("Access-Control-Allow-Headers", corsHeaders)
        .build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(
      @QueryParam("schema") Boolean schema,
      @HeaderParam(HttpHeaders.AUTHORIZATION) String auth,
      @QueryParam("auth_token") String queryAuthToken,
      @QueryParam("q") String querystr,
      @QueryParam("op") String opname) throws IOException
  {

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

    // try (RequestContext ctx = core.open(RequestType.QUERY, authValue))
    // {
    // return Response.status(200).entity(ctx.query(query, GQLValues.objectValue())).build();
    // }

    GQLObjectValue res = root.execute(query, null);

    return Response.status(200).entity(res)
        .header("Access-Control-Allow-Origin", "*")
        .header("Access-Control-Allow-Credentials", "true")
        .build();

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
  @Consumes("application/graphql")
  @Produces(MediaType.APPLICATION_JSON)
  public Response simplePost(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("auth_token") String queryAuthToken, String query) throws Exception
  {
    return get(false, auth, queryAuthToken, query, null);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response post(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("auth_token") String queryAuthToken, HttpGraphQLQueryData body)
  {

    RegistryAuthValue authValue = RegistryHttpUtils.parseAuth(auth);

    if (authValue == null)
    {
      authValue = RegistryBearerAuthValue.fromToken(queryAuthToken);
    }
    else if (body != null && body.access_token != null)
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

    GQLObjectValue res = root.execute(query, null);

    // .execute(QueryEnvironment.emptyEnvironment(), query, null);

    return Response.status(200)
        .header("Access-Control-Allow-Origin", "*")
        .header("Access-Control-Allow-Credentials", "true")
        .entity(res).build();

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
package io.joss.graphql.jersey.resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
import com.google.common.io.Files;
import com.google.common.net.HttpHeaders;

import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLOpType;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.parser.GQLException;
import io.joss.graphql.core.parser.GQLParser;
import io.joss.graphql.core.parser.SyntaxErrorException;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.jersey.GQLJacksonUtils;
import io.joss.graphql.jersey.RegistryHttpUtils;
import io.joss.graphql.jersey.auth.RegistryAuthValue;
import io.joss.graphql.jersey.auth.RegistryBearerAuthValue;
import io.joss.graphql.jersey.http.HttpErrorMessage;
import io.joss.graphql.jersey.http.Position;
import lombok.Data;
import lombok.SneakyThrows;

@Path("/api/graphql")
public class GraphQLEngineResource
{

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

    if (schema != null)
    {
      querystr = new String(Files.toByteArray(new File(getClass().getResource("/introspection.gql").getFile())), StandardCharsets.UTF_8);
    }

    if (querystr == null || querystr.trim().isEmpty())
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.MISSING_PARAMETER, "query required")).build();
    }

    final GQLDocument doc;
    try
    {
      doc = GQLParser.parseDocument(getQuery(querystr));
    }
    catch (SyntaxErrorException ex)
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.GQL_QUERY_SYNTAX_ERROR, ex.getMessage(), Position.create(ex))).build();
    }
    catch (GQLException ex)
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.GQL_QUERY_SYNTAX_ERROR, ex.getMessage())).build();
    }

    if (doc.definitions().isEmpty())
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.MISSING_PARAMETER, "query required")).build();
    }

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
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.INVALID_OP_TYPE, "Only query is alllwed over HTTP GET")).build();
    }

    try
    {
      GQLObjectValue res = root.execute(params(auth, queryAuthToken), query, null);

      return Response.status(200).entity(res)
          .header("Access-Control-Allow-Origin", "*")
          .header("Access-Control-Allow-Credentials", "true")
          .build();

    }
    catch (GQLException ex)
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.GQL_QUERY_SYNTAX_ERROR, ex.getMessage())).build();
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
  @Consumes("application/graphql")
  @Produces(MediaType.APPLICATION_JSON)
  public Response simplePost(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String auth,
      @QueryParam("auth_token") String queryAuthToken,
      String query) throws Exception
  {
    return get(null, auth, queryAuthToken, query, null);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response post(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String auth,
      @QueryParam("auth_token") String queryAuthToken,
      HttpGraphQLQueryData body)
  {

    final GQLDocument doc;
    try
    {
      doc = GQLParser.parseDocument(getQuery(body.query));
    }
    catch (SyntaxErrorException ex)
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.GQL_QUERY_SYNTAX_ERROR, ex.getMessage())).build();
    }
    catch (GQLException ex)
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.GQL_QUERY_SYNTAX_ERROR, ex.getMessage())).build();
    }

    final GQLSelectedOperation query;

    try
    {
      query = GQLSelectedOperation.namedQuery(doc, body.operation);
    }
    catch (IllegalArgumentException ex)
    {
      return Response.status(400).entity(new HttpErrorMessage(HttpErrorCodes.INVALID_OPERATION, String.format("Invalid operation '%s'", body.operation))).build();
    }

    GQLObjectValue input = (GQLObjectValue) GQLJacksonUtils.convertToGQL(body.variables);
    GQLObjectValue res = root.execute(params(auth, body.access_token == null ? queryAuthToken : body.access_token), query, input);

    return Response.status(200)
        .header("Access-Control-Allow-Origin", "*")
        .header("Access-Control-Allow-Credentials", "true")
        .entity(res).build();

  }

  /**
   * 
   */

  private GraphQLHttpParams params(String authHeader, String token)
  {

    RegistryAuthValue authValue = RegistryHttpUtils.parseAuth(authHeader);

    if (authValue == null)
    {
      if (token != null)
      {
        authValue = RegistryBearerAuthValue.fromToken(token);
      }
    }

    return GraphQLHttpParams.builder()
        .auth(authValue)
        .build();

  }

}
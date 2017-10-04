package io.zrz.graphql.jersey.resource;


public class HttpErrorCodes
{

  // client HTTP/transport errors.
  public static final int JSON_ERROR = 1000;
  public static final int INVALID_OPERATION = 1001;
  public static final int INVALID_OP_TYPE = 1002;
  public static final int AUTH_REQUIRED = 1003;
  public static final int MISSING_PARAMETER = 1004;

  public static final int GQL_QUERY_SYNTAX_ERROR = 2000;

  // an opaque error given to client
  public static final int INTERNAL_ERROR = 5000;

}
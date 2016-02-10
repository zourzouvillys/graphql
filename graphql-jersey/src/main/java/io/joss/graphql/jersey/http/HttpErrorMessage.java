package io.joss.graphql.jersey.http;

import lombok.Value;

@Value
public class HttpErrorMessage
{
  private int code;
  private String message;
}
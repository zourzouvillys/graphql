package io.joss.graphql.jersey.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class HttpErrorMessage
{
  
  private final int code;
  private final String message;
  
  private Position position;
  
}
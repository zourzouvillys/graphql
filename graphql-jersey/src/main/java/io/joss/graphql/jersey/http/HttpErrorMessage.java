package io.joss.graphql.jersey.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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

  @JsonInclude(Include.NON_NULL)
  private Position position;
  
}
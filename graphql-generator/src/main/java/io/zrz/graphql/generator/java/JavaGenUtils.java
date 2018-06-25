package io.zrz.graphql.generator.java;

import org.apache.commons.lang3.StringUtils;

public class JavaGenUtils {

  public static String methodName(String operationName) {
    operationName = StringUtils.uncapitalize(operationName);
    return operationName;
  }

  public static String paramName(String name) {
    name = StringUtils.uncapitalize(name);
    return name;
  }

  public static String className(final String operationName) {
    return StringUtils.capitalize(operationName);
  }

}

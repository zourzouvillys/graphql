package io.zrz.graphql.zulu.executable;

public class GraphQLUtils {

  public static boolean isBuiltinScalar(final String typeName) {

    switch (typeName) {
      case "Int":
      case "Boolean":
      case "Float":
      case "String":
        return true;
    }

    return false;

  }

}

package io.zrz.zulu.schema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.common.io.Resources;

import io.zrz.graphql.core.doc.GQLOpType;

public class SchemaCompilerTest {

  @Test
  public void test() throws IOException {

    SchemaCompiler compiler = new SchemaCompiler();

    compiler.addUnit(Resources.asCharSource(Resources.getResource(getClass(), "/github.schema"), StandardCharsets.UTF_8));

    ResolvedSchema types = compiler.compile("Query", "Mutation");

    ResolvedType t = types.operationType(GQLOpType.Query);

    ((ResolvedObjectType) t).fields().forEach(f -> System.err.println(f.fieldName() + ": " + f.returnType()));

  }

}

package io.zrz.zulu.validator;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.zulu.schema.ResolvedSchema;
import io.zrz.zulu.schema.SchemaCompiler;
import io.zrz.zulu.schema.binding.BoundDocument;
import io.zrz.zulu.schema.validation.ValidationCollector;

public class DocumentValidatorTest {

  @Test
  public void test() {

    final SchemaCompiler compiler = new SchemaCompiler();

    compiler.addUnit("type HelloWorldService { hello(name: String): String }");

    final Map<GQLOperationType, String> ops = ImmutableMap.of(GQLOpType.Query, "HelloWorldService");

    final ResolvedSchema schema = new ResolvedSchema(compiler, ops);

    final DocumentValidator validator = new DocumentValidator(schema);

    final ValidationCollector<String> collector = new ValidationCollector<>();

    final BoundDocument bound = validator.validate("query getHello($a: String!) { hello(name: $a) }", collector);

    System.err.println(bound);

    collector.diagnostics()
        .forEach(System.err::println);

  }

}

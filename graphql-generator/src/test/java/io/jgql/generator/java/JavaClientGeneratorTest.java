package io.jgql.generator.java;

import org.junit.Test;

import io.jgql.generator.java.modeltest.TestClientQueryRoot;
import io.joss.graphql.core.binder.TypeBindingResult;
import io.joss.graphql.core.binder.TypeScanner;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.parser.GQLParser;
import io.joss.graphql.generator.java.JavaClientGenerator;

public class JavaClientGeneratorTest
{

  @Test
  public void test()
  {

    // bind the schema to TestQueryRoot
    TypeBindingResult result = TypeScanner.bind(TestClientQueryRoot.class);

    // now, generate the queries.    
    GQLDocument doc = GQLParser.parseDocument(getClass().getResourceAsStream("/test.gql"));
    
    
    // and generate the java code ...
    JavaClientGenerator gen = new JavaClientGenerator(result.registry(), result.root(), doc);
    
    gen.generate("MyTest", System.err);
    

  }

}

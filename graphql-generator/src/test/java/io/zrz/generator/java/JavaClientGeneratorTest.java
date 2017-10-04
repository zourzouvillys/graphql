package io.zrz.generator.java;

import org.junit.Test;

import io.zrz.generator.java.modeltest.TestClientQueryRoot;
import io.zrz.graphql.core.binder.TypeBindingResult;
import io.zrz.graphql.core.binder.TypeScanner;
import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.parser.GQLParser;
import io.zrz.graphql.generator.java.JavaClientGenerator;

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

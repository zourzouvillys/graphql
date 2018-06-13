package io.zrz.graphql.zulu.java;

import org.junit.Test;

import io.zrz.graphql.zulu.binding.JavaBindingProvider;

public class JavaClassContributionGeneratorTest {

  @Test
  public void testAllowDuplicateTypeRegistration() {

    JavaBindingProvider j = new JavaBindingProvider();

    j.registerType(String.class);

    // second one should be allowed
    j.registerType(String.class);

  }

}

package io.jgql.core.binder.testmodel;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.core.binder.annotatons.GQLTypeUse;

@GQLType
public interface TestInterface
{

  /**
   * A field which returns a type of ID, even through it's returning a String. The registered scalar converter for the ID type will receive
   * the returned value to convert it.
   * 
   * Alternatively, the method could have returned an GQLIDValue type if it preferred to deal with the encoding itself. It seems to keep the
   * code cleaner like this though, when you're trying to overlay GQL on top of existing classes.
   * 
   */

  @GQLField
  @GQLNonNull
  @GQLTypeUse("ID")
  String getId();

}

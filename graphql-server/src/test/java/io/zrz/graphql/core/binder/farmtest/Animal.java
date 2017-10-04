package io.zrz.graphql.core.binder.farmtest;

import io.zrz.graphql.core.binder.annotatons.GQLArg;
import io.zrz.graphql.core.binder.annotatons.GQLDefaultValue;
import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLNonNull;
import io.zrz.graphql.core.binder.annotatons.GQLType;

@GQLType
public class Animal
{

  private String singular;
  private String plural;
  private String sound;

  public Animal(String singular, String plural, String sound)
  {
    this.singular = singular;
    this.plural = plural;
    this.sound = sound;
  }

  
  @GQLField
  public @GQLNonNull String getName(@GQLArg("plural") @GQLDefaultValue("false") boolean plural)
  {
    return (plural) ? this.plural : this.singular;
  }

  @GQLField
  public @GQLNonNull String getSound()
  {
    return this.sound;
  }

}

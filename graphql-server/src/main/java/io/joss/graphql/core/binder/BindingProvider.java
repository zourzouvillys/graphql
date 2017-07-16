package io.joss.graphql.core.binder;

import io.joss.graphql.core.binder.model.OutputClassBinding;
import io.joss.graphql.core.decl.GQLTypeDeclaration;

public interface BindingProvider
{

  OutputClassBinding meta(GQLTypeDeclaration declaration);

}

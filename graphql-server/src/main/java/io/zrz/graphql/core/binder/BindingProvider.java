package io.zrz.graphql.core.binder;

import io.zrz.graphql.core.binder.model.OutputClassBinding;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;

public interface BindingProvider
{

  OutputClassBinding meta(GQLTypeDeclaration declaration);

}

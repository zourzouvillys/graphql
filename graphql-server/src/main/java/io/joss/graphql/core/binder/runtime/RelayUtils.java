package io.joss.graphql.core.binder.runtime;

import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.decl.GQLDeclarationVisitors;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.types.GQLTypeReference;

public class RelayUtils
{

  /**
   * 
   */

  public static class RelayConnectionContext
  {

    private DataContext ctx;
    private RelayConnectionEdge edge;

    public RelayConnectionContext(DataContext ctx)
    {
      this.ctx = ctx;
      this.edge = new RelayConnectionEdge(this, ctx.child("edges"));
    }

    public RelayConnectionEdge edge()
    {
      return this.edge;
    }

    public GQLObjectTypeDeclaration decl()
    {
      return ((GQLObjectTypeDeclaration) ctx.declaration());
    }

    public DataContext context()
    {
      return this.ctx;
    }

  }

  public static class RelayConnectionEdge
  {

    private DataContext ctx;
    private RelayConnectionContext parent;
    private DataContext nodeCtx;

    public RelayConnectionEdge(RelayConnectionContext parent, DataContext ctx)
    {
      if (ctx == null)
      {
        throw new IllegalArgumentException("ctx");
      }
      this.parent = parent;
      this.ctx = ctx;
      this.nodeCtx = ctx.child("node");
    }

    public RelayConnectionContext connection()
    {
      return this.parent;
    }

    public DataContext context()
    {
      return this.ctx;
    }

    public GQLTypeDeclaration edgeType()
    {
      return nodeCtx.declaration();
    }

    public GQLTypeReference type()
    {
      return ctx.type();
    }

    public GQLTypeDeclaration decl()
    {
      return ctx.declaration();
    }

  }

  /**
   * true if this context looks like it's a relay edge.
   */

  public static RelayConnectionContext toRelayConnection(DataContext ctx)
  {
    return new RelayConnectionContext(ctx);
  }

  public static boolean isRelayNode(DataContext ctx)
  {
    return ctx.declaration().apply(GQLDeclarationVisitors.isRelayNode());
  }

}

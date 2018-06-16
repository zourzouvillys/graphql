package io.zrz.graphql.zulu.engine;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;

import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;

public class ZuluLeafSelection extends AbstractZuluSelection implements ZuluSelection {

  private MethodHandle handle;
  private Optional<? extends AnnotatedElement> origin;

  public ZuluLeafSelection(ZuluSelectionContainer parent, ExecutableOutputField field, GQLPreparedSelection sel, MethodHandle handle) {
    super(field, sel, parent.outputType());
    this.handle = Objects.requireNonNull(handle);
    Preconditions.checkState(sel.isLeaf());
    this.origin = field.origin();
  }

  @Override
  public MethodHandle invoker() {
    return this.handle;
  }

  @Override
  public void apply(ZuluSelectionVisitor.VoidVisitor visitor) {
    visitor.accept(this);
  }

  @Override
  public <T> void apply(ZuluSelectionVisitor.ConsumerVisitor<T> visitor, T value) {
    visitor.accept(this, value);
  }

  @Override
  public <R> R apply(ZuluSelectionVisitor.SupplierVisitor<R> visitor) {
    return visitor.accept(this);

  }

  @Override
  public <T, R> R apply(ZuluSelectionVisitor.FunctionVisitor<T, R> visitor, T value) {
    return visitor.accept(this, value);
  }

  @Override
  public Optional<? extends AnnotatedElement> origin() {
    return origin;
  }

}

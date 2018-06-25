package io.zrz.graphql.zulu.engine;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;

import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableReceiverType;

public class ZuluLeafSelection extends AbstractZuluSelection implements ZuluSelection {

  private final TypeTokenMethodHandle handle;
  private final Optional<? extends AnnotatedElement> origin;
  private final ExecutableReceiverType receiverType;

  public ZuluLeafSelection(final ZuluSelectionContainer parent, final ExecutableOutputField field, final GQLPreparedSelection sel,
      final TypeTokenMethodHandle handle, final ExecutableReceiverType receiverType) {
    super(field, sel, parent.outputType());
    this.handle = Objects.requireNonNull(handle);
    Preconditions.checkState(sel.isLeaf());
    this.origin = field.origin();
    this.receiverType = receiverType;
  }

  @Override
  public MethodHandle invoker() {
    return this.handle.handle();
  }

  @Override
  public void apply(final ZuluSelectionVisitor.VoidVisitor visitor) {
    visitor.accept(this);
  }

  @Override
  public <T> void apply(final ZuluSelectionVisitor.ConsumerVisitor<T> visitor, final T value) {
    visitor.accept(this, value);
  }

  @Override
  public <R> R apply(final ZuluSelectionVisitor.SupplierVisitor<R> visitor) {
    return visitor.accept(this);

  }

  @Override
  public <T, R> R apply(final ZuluSelectionVisitor.FunctionVisitor<T, R> visitor, final T value) {
    return visitor.accept(this, value);
  }

  @Override
  public Optional<? extends AnnotatedElement> origin() {
    return this.origin;
  }

}

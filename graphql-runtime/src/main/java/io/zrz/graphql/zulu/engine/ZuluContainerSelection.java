package io.zrz.graphql.zulu.engine;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableReceiverType;

public class ZuluContainerSelection extends AbstractZuluSelection implements ZuluSelection, ZuluSelectionContainer {

  private final ZuluSelectionContainer parent;
  private final ExecutableOutputField field;
  private final GQLPreparedSelection sel;
  private final ZuluExecutable executable;
  private final ImmutableList<ZuluSelection> selections;
  private final ExecutableReceiverType returnType;
  private final TypeTokenMethodHandle handle;
  private final ExecutableReceiverType receiverType;

  public ZuluContainerSelection(
      final ZuluSelectionContainer parent,
      final ExecutableOutputField field,
      final GQLPreparedSelection sel,
      final TypeTokenMethodHandle handle,
      final ExecutableReceiverType receiverType,
      final ExecutableBuilder b) {

    super(field, sel, parent.outputType());

    Preconditions.checkState(!sel.isLeaf());

    this.parent = parent;
    this.field = field;
    this.sel = sel;
    this.executable = parent.executable();
    this.returnType = (ExecutableReceiverType) this.field.fieldType().type();
    this.handle = Objects.requireNonNull(handle);
    this.selections = b.build(this, sel.subselections());
    this.receiverType = receiverType;

  }

  public ImmutableList<ZuluSelection> selections() {
    return this.selections;
  }

  @Override
  public ZuluExecutable executable() {
    return this.executable;
  }

  /**
   * the type which this selection will be made against. this is different from the context type when there is a spread
   * on another type.
   */

  public ExecutableReceiverType receiverType() {
    return this.receiverType;
  }

  /**
   * the output type for a container is the type of the container itself, e.g the result type from executing the field.
   */

  @Override
  public ExecutableReceiverType outputType() {
    return this.returnType;
  }

  /**
   * invocation handle which has a signature of (ZuluResultReceiver,T)C
   */

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
    return this.field.invoker().origin();
  }

}

package io.zrz.graphql.zulu.engine;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputType;

public class ZuluContainerSelection extends AbstractZuluSelection implements ZuluSelection, ZuluSelectionContainer {

  private ZuluSelectionContainer parent;
  private ExecutableOutputField field;
  private GQLPreparedSelection sel;
  private ZuluExecutable executable;
  private ImmutableList<ZuluSelection> selections;
  private ExecutableOutputType returnType;
  private MethodHandle handle;

  public ZuluContainerSelection(
      ZuluSelectionContainer parent,
      ExecutableOutputField field,
      GQLPreparedSelection sel,
      MethodHandle handle,
      ExecutableBuilder b) {

    super(field, sel, parent.outputType());

    Preconditions.checkState(!sel.isLeaf());

    this.parent = parent;
    this.field = field;
    this.sel = sel;
    this.executable = parent.executable();
    this.returnType = (ExecutableOutputType) this.field.fieldType().type();
    this.handle = Objects.requireNonNull(handle);
    this.selections = b.build(this, sel.subselections());

  }

  public ImmutableList<ZuluSelection> selections() {
    return this.selections;
  }

  @Override
  public ZuluExecutable executable() {
    return this.executable;
  }

  /**
   * the output type for a container is the type of the container itself, e.g the result type from executing the field.
   */

  @Override
  public ExecutableOutputType outputType() {
    return returnType;
  }

  /**
   * invocation handle which has a signature of (ZuluResultReceiver,T)C
   */

  @Override
  public MethodHandle invoker() {
    return handle;
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
    return this.field.invoker().origin();
  }

}

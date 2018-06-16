package io.zrz.graphql.zulu.engine;

import io.zrz.graphql.core.parser.GQLException;
import io.zrz.graphql.core.parser.GQLSourceLocation;
import io.zrz.graphql.core.parser.ImmutableGQLSourceLocation;
import io.zrz.graphql.core.parser.ImmutableLineInfo;
import io.zrz.graphql.core.parser.SyntaxErrorException;
import io.zrz.graphql.zulu.doc.GQLPreparedDocument;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.executable.ExecutableElement;
import io.zrz.graphql.zulu.executable.ExecutableInputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputType;
import io.zrz.graphql.zulu.executable.ExecutableType;
import io.zrz.zulu.types.ZField;

public interface ZuluWarning {

  public class IncompatibleTypes extends AbstractWarning<ExecutableInputField> {

    private ZField provided;

    public IncompatibleTypes(ExecutableInputField param, ZField provided, GQLPreparedSelection sel) {
      super(ZuluWarningKind.INCOMPATIBLE_TYPE, param, sel);
      this.provided = provided;
    }

    @Override
    public String detail() {
      return "input of type " + provided.fieldType() + " for argument '" + type.fieldName() + "' of "
          + type.enclosingType().typeName()
          + "."
          + type.enclosingField().fieldName()
          + " is incompatible with required type "
          + type.fieldType().logicalType();
    }

    @Override
    public ExecutableType context() {
      return this.type.enclosingType();
    }

  }

  public class DocumentWarning implements ZuluWarning {

    private GQLPreparedDocument doc;
    private ZuluWarningKind kind;
    private String operationName;

    public DocumentWarning(ZuluWarningKind kind, GQLPreparedDocument doc, String operationName) {
      this.doc = doc;
      this.kind = kind;
      this.operationName = operationName;
    }

    public DocumentWarning(ZuluWarningKind kind, GQLPreparedDocument doc) {
      this(kind, doc, null);
    }

    @Override
    public String detail() {
      return kind.detail(this);
    }

    public String operationName() {
      return this.operationName;
    }

    @Override
    public ExecutableType context() {
      return null;
    }

    @Override
    public ZuluWarningKind warningKind() {
      return this.kind;
    }

    @Override
    public GQLSourceLocation sourceLocation() {
      return null;
    }

    @Override
    public GQLPreparedSelection selection() {
      return null;
    }

    @Override
    public Throwable cause() {
      return null;
    }

  }

  public class ParseWarning implements ZuluWarning {

    private ZuluWarningKind kind;
    private String input;
    private GQLException error;

    public ParseWarning(ZuluWarningKind kind, String input, GQLException error) {
      this.error = error;
      this.input = input;
      this.kind = kind;
    }

    @Override
    public String detail() {
      return kind.detail(this);
    }

    public String input() {
      return this.input;
    }

    @Override
    public ExecutableType context() {
      return null;
    }

    @Override
    public ZuluWarningKind warningKind() {
      return this.kind;
    }

    @Override
    public GQLSourceLocation sourceLocation() {

      if (error instanceof SyntaxErrorException) {

        SyntaxErrorException err = (SyntaxErrorException) error;

        ImmutableLineInfo info = err.lineInfo();

        return ImmutableGQLSourceLocation.builder()
            .lineNumber(info.lineNumber())
            .lineOffset(info.lineOffset() + 1)
            .input(info.source())
            .sourceOffset(0)
            .build();

      }
      return null;
    }

    @Override
    public GQLPreparedSelection selection() {
      return null;
    }

    @Override
    public Throwable cause() {
      return error;
    }

  }

  ZuluWarningKind warningKind();

  String detail();

  GQLSourceLocation sourceLocation();

  ExecutableType context();

  GQLPreparedSelection selection();

  Throwable cause();

  abstract class AbstractWarning<T extends ExecutableElement> implements ZuluWarning {

    protected ZuluWarningKind kind;
    protected GQLPreparedSelection sel;
    protected T type;
    protected Throwable cause;

    public AbstractWarning(ZuluWarningKind kind, T type) {
      this.kind = kind;
      this.type = type;
    }

    public AbstractWarning(ZuluWarningKind kind, T type, GQLPreparedSelection sel) {
      this.kind = kind;
      this.sel = sel;
      this.type = type;
    }

    public AbstractWarning(ZuluWarningKind kind, ZuluSelection sel, T type) {
      this.kind = kind;
      this.sel = sel;
      this.type = type;
    }

    @Override
    public GQLSourceLocation sourceLocation() {
      return sel.sourceLocation();
    }

    public T element() {
      return this.type;
    }

    @Override
    public ZuluWarningKind warningKind() {
      return kind;
    }

    @Override
    public String detail() {
      return kind.toString();
    }

    @Override
    public GQLPreparedSelection selection() {
      return sel;
    }

    @Override
    public Throwable cause() {
      return cause;
    }

    @Override
    public String toString() {

      StringBuilder sb = new StringBuilder();

      if (sourceLocation() != null) {
        sb.append("(line ");
        sb.append(sourceLocation().lineNumber());
        sb.append(", col ");
        sb.append(sourceLocation().lineOffset());
        sb.append(", selection ");
        sb.append(selection().path());
        sb.append("): ");
      }

      sb.append(detail());

      return sb.toString();
    }

  }

  public class OutputTypeWarning extends AbstractWarning<ExecutableOutputType> {

    private ExecutableOutputType type;

    public OutputTypeWarning(ZuluWarningKind kind, ExecutableOutputType type, GQLPreparedSelection sel) {
      super(kind, type, sel);
      this.type = type;
    }

    @Override
    public ExecutableType context() {
      return this.type;
    }

    @Override
    public String detail() {
      return kind.detail(this);
    }

  }

  public class OutputFieldWarning extends AbstractWarning<ExecutableOutputField> {

    private String message;

    public OutputFieldWarning(ZuluWarningKind kind, ExecutableOutputField field, GQLPreparedSelection sel) {
      super(kind, field, sel);

    }

    public OutputFieldWarning(ZuluWarningKind kind, ExecutableOutputField field, GQLPreparedSelection sel, String message) {
      super(kind, field, sel);
      this.message = message;
    }

    @Override
    public ExecutableOutputType context() {
      return this.type.receiverType();
    }

    @Override
    public String detail() {
      if (this.message != null) {
        return this.message;
      }
      return kind.detail(this);
    }

  }

  public class MissingField extends AbstractWarning<ExecutableOutputType> {

    public MissingField(ExecutableOutputType type, GQLPreparedSelection sel) {
      super(ZuluWarningKind.UNKNOWN_FIELD, type, sel);
    }

    @Override
    public String detail() {
      return "field " + type.typeName() + "." + sel.fieldName() + " doesn't exist";
    }

    @Override
    public ExecutableType context() {
      return this.type;
    }

    @Override
    public GQLPreparedSelection selection() {
      return sel;
    }

  }

  public class MissingRequiredParameter extends AbstractWarning<ExecutableInputField> {

    private ExecutableInputField param;

    public MissingRequiredParameter(ExecutableInputField param, GQLPreparedSelection sel) {
      super(ZuluWarningKind.MISSING_PARAMETER, param, sel);
      this.param = param;
    }

    @Override
    public String detail() {
      return "missing required parameter '" + param.fieldName() + "' for "
          + param.enclosingType().typeName()
          + "."
          + param.enclosingField().fieldName();
    }

    @Override
    public ExecutableType context() {
      return this.param.enclosingType();
    }

  }

  public class ExecutionError extends AbstractWarning<ExecutableElement> {

    private Throwable error;
    private Object context;
    private ExecutableOutputType type;
    private ZuluSelection selection;

    public ExecutionError(ZuluLeafSelection leaf, Throwable ex, Object context) {
      super(ZuluWarningKind.INVOCATION_EXCEPTION, leaf, leaf.element());
      this.error = ex;
      this.context = context;
      this.type = leaf.contextType();
      this.selection = leaf;
    }

    public ExecutionError(ZuluContainerSelection container, Throwable ex, Object context) {
      super(ZuluWarningKind.INVOCATION_EXCEPTION, container, container.element());
      this.error = ex;
      this.context = context;
      this.type = container.contextType();
      this.selection = container;
    }

    @Override
    public ZuluSelection selection() {
      return this.selection;
    }

    @Override
    public ExecutableType context() {
      return this.type;
    }

    @Override
    public Throwable cause() {
      return this.error;
    }

    @Override
    public String detail() {
      if (error.getMessage() == null) {
        return error.toString();
      }
      return error.getMessage();
    }

  }

}

package io.zrz.graphql.zulu.engine;

import java.util.Objects;

import io.zrz.graphql.core.parser.GQLException;
import io.zrz.graphql.core.parser.GQLSourceLocation;
import io.zrz.graphql.core.parser.ImmutableGQLSourceLocation;
import io.zrz.graphql.core.parser.ImmutableLineInfo;
import io.zrz.graphql.core.parser.SyntaxErrorException;
import io.zrz.graphql.zulu.doc.DefaultGQLPreparedOperation.OpInputField;
import io.zrz.graphql.zulu.doc.GQLPreparedDocument;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.executable.ExecutableElement;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputFieldParam;
import io.zrz.graphql.zulu.executable.ExecutableReceiverType;
import io.zrz.graphql.zulu.executable.ExecutableType;
import io.zrz.zulu.types.ZField;

public interface ZuluWarning {

  public class IncompatibleTypes extends AbstractWarning<ExecutableOutputFieldParam> {

    private final ZField provided;

    public IncompatibleTypes(final ExecutableOutputFieldParam param, final ZField provided, final GQLPreparedSelection sel) {
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

  public class UnknownTypeSymbol extends AbstractWarning<ExecutableElement> {

    private final String symbol;

    public UnknownTypeSymbol(final ExecutableElement context, final String symbol, final GQLPreparedSelection sel) {
      super(ZuluWarningKind.UNKNOWN_TYPE, context, sel);
      this.symbol = symbol;
    }

    @Override
    public String detail() {
      return "type '" + symbol + "' is unknown";
    }

    @Override
    public ExecutableType context() {
      return null;
    }

  }

  public class DocumentWarning implements ZuluWarning {

    private final GQLPreparedDocument doc;
    private final ZuluWarningKind kind;
    private final String operationName;

    public DocumentWarning(final ZuluWarningKind kind, final GQLPreparedDocument doc, final String operationName) {
      this.doc = doc;
      this.kind = kind;
      this.operationName = operationName;
    }

    public DocumentWarning(final ZuluWarningKind kind, final GQLPreparedDocument doc) {
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

    private final ZuluWarningKind kind;
    private final String input;
    private final GQLException error;

    public ParseWarning(final ZuluWarningKind kind, final String input, final GQLException error) {
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

        final SyntaxErrorException err = (SyntaxErrorException) error;

        final ImmutableLineInfo info = err.lineInfo();

        if (info == null) {
          return null;
        }

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

  ExecutableElement context();

  GQLPreparedSelection selection();

  Throwable cause();

  abstract class AbstractWarning<T extends ExecutableElement> implements ZuluWarning {

    protected ZuluWarningKind kind;
    protected GQLPreparedSelection sel;
    protected T type;
    protected Throwable cause;

    public AbstractWarning(final ZuluWarningKind kind, final T type) {
      this.kind = kind;
      this.type = type;
    }

    public AbstractWarning(final ZuluWarningKind kind, final T type, final GQLPreparedSelection sel) {
      this.kind = kind;
      this.sel = sel;
      this.type = type;
    }

    public AbstractWarning(final ZuluWarningKind kind, final ZuluSelection sel, final T type) {
      this.kind = kind;
      this.sel = sel;
      this.type = type;
    }

    @Override
    public GQLSourceLocation sourceLocation() {
      if (sel != null) {
        return sel.sourceLocation();
      }
      return null;
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

      final StringBuilder sb = new StringBuilder();

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

  public class OutputTypeWarning extends AbstractWarning<ExecutableReceiverType> {

    private final ExecutableReceiverType type;

    public OutputTypeWarning(final ZuluWarningKind kind, final ExecutableReceiverType type, final GQLPreparedSelection sel) {
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

    public OutputFieldWarning(final ZuluWarningKind kind, final ExecutableOutputField field, final GQLPreparedSelection sel) {
      super(kind, field, sel);

    }

    public OutputFieldWarning(final ZuluWarningKind kind, final ExecutableOutputField field, final GQLPreparedSelection sel, final String message) {
      super(kind, field, sel);
      this.message = message;
    }

    @Override
    public ExecutableType context() {
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

  public class MissingField extends AbstractWarning<ExecutableReceiverType> {

    public MissingField(final ExecutableReceiverType type, final GQLPreparedSelection sel) {
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

  public class MissingRequiredParameter extends AbstractWarning<ExecutableOutputFieldParam> {

    private final ExecutableOutputFieldParam param;

    public MissingRequiredParameter(final ExecutableOutputFieldParam param, final GQLPreparedSelection sel) {
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

  public class MissingRequiredVariable extends AbstractWarning<ZuluExecutable> {

    private final OpInputField param;

    public MissingRequiredVariable(final OpInputField param, final ZuluExecutable executable) {
      super(ZuluWarningKind.MISSING_VARIABLE, executable);
      this.param = param;
    }

    @Override
    public String detail() {
      return "missing required input variable '" + param.fieldName() + "'";
    }

    @Override
    public ExecutableElement context() {
      return null;
    }

  }

  public class ExecutionError extends AbstractWarning<ExecutableElement> {

    private final Throwable error;
    private final Object context;
    private final ExecutableReceiverType type;
    private final ZuluSelection selection;

    public ExecutionError(final ZuluLeafSelection leaf, final Throwable ex, final Object context) {
      super(ZuluWarningKind.INVOCATION_EXCEPTION, leaf, leaf.element());
      this.error = ex;
      this.context = context;
      this.type = leaf.contextType();
      this.selection = leaf;
    }

    public ExecutionError(final ZuluContainerSelection container, final Throwable ex, final Object context) {
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

  public class InternalError extends AbstractWarning<ExecutableElement> {

    private final Throwable error;
    private final String detail;
    private final ExecutableReceiverType etype;

    public InternalError(final ExecutableOutputFieldParam param, final GQLPreparedSelection sel, final String message) {
      super(ZuluWarningKind.INTERNAL_ERROR, param, sel);
      this.etype = param.enclosingType();
      this.detail = Objects.requireNonNull(message);
      try {
        throw new RuntimeException();
      }
      catch (final RuntimeException ex) {
        this.error = ex;
      }
    }

    @Override
    public ExecutableType context() {
      return this.etype;
    }

    @Override
    public Throwable cause() {
      return this.error;
    }

    @Override
    public String detail() {
      if (this.detail != null) {
        return this.detail;
      }
      if (error.getMessage() == null) {
        return error.toString();
      }
      return error.getMessage();
    }

  }

}

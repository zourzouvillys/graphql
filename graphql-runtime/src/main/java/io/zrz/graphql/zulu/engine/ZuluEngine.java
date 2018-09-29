package io.zrz.graphql.zulu.engine;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.Hashing;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.parser.GQLException;
import io.zrz.graphql.core.parser.GQLSourceLocation;
import io.zrz.graphql.core.parser.GQLSourceRange;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.doc.CachingGQLDocumentManager;
import io.zrz.graphql.zulu.doc.GQLDocumentManager;
import io.zrz.graphql.zulu.doc.GQLPreparedDocument;
import io.zrz.graphql.zulu.doc.GQLPreparedOperation;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.doc.GQLPreparedValidationListener;
import io.zrz.graphql.zulu.executable.ExecutableElement;
import io.zrz.graphql.zulu.executable.ExecutableInput;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;
import io.zrz.graphql.zulu.executable.JavaExecutableUtils;
import io.zrz.graphql.zulu.server.ImmutableBindParams;
import io.zrz.graphql.zulu.server.ImmutableQuery;
import io.zrz.graphql.zulu.server.ImmutableZuluServerRequest;
import io.zrz.graphql.zulu.server.ZuluInjector;
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZTypeUse;
import io.zrz.zulu.values.ZArrayValue;
import io.zrz.zulu.values.ZBoolValue;
import io.zrz.zulu.values.ZDoubleValue;
import io.zrz.zulu.values.ZIntValue;
import io.zrz.zulu.values.ZScalarValue;
import io.zrz.zulu.values.ZStringValue;
import io.zrz.zulu.values.ZValue;
import zulu.runtime.subscriptions.ZuluSubscriptionContext;

public class ZuluEngine {

  private static Logger log = LoggerFactory.getLogger(ZuluEngine.class);

  private final Cache<String, ZuluCompileResult> cache = CacheBuilder.newBuilder()
      .maximumSize(1_000)
      .build();

  // private final Map<String, ZuluCompileResult> cache = new HashMap<>();
  private final ExecutableSchema schema;
  private final GQLDocumentManager docs;

  public ZuluEngine(final Type queryRoot) {
    this(ExecutableSchema.builder().setRootType(GQLOpType.Query, queryRoot).build(false));
  }

  public ZuluEngine(final ExecutableSchema schema) {
    this(schema, null);
  }

  public ZuluEngine(final ExecutableSchema schema, final GQLDocumentManager docmgr) {
    this.schema = Objects.requireNonNull(schema);

    if (docmgr == null) {
      System.err.println("schema: " + schema);
      this.docs = new CachingGQLDocumentManager(schema);
    }
    else {
      this.docs = Objects.requireNonNull(docmgr);
    }

  }

  /**
   * execute a prepared operation.
   *
   * @return
   */

  public ZuluCompileResult compile(final GQLPreparedOperation op) {
    final ExecutableBuilder builder = new ExecutableBuilder(this, op);
    return new ZuluCompileResult(op, builder, builder.build());
  }

  /**
   * execute a simple unnamed query.
   *
   * @return
   */

  public ZuluCompileResult compile(final String queryString) {
    return this.compile(this.docs.prepareDocument(this.docs.parse(queryString)).defaultOperation().get());
  }

  /**
   *
   * @param documentString
   * @return
   */

  public Stream<ZuluCompileResult> compileDocument(final String documentString) {
    final GQLPreparedDocument doc = this.docs.prepareDocument(this.docs.parse(documentString));
    return doc
        .operations()
        .map(op -> this.compile(op));
  }

  /**
   *
   * @param queryString
   * @param operationName
   * @return
   */

  public ZuluCompileResult compile(final String queryString, final String operationName, final String persistedQuery) {

    if (persistedQuery != null) {

      final ZuluCompileResult cached = this.cache.getIfPresent(persistedQuery);

      if (cached != null) {
        // woo - cache hit.
        return cached;
      }

      if (queryString == null) {
        // add warn?
        return ZuluCompileResult.withErrors(
            new ZuluWarning.ParseWarning(ZuluWarningKind.PERSISTED_QUERY_NOT_FOUND, queryString, null));
      }

    }

    if (queryString == null) {
      return ZuluCompileResult.withErrors(
          new ZuluWarning.ParseWarning(ZuluWarningKind.SYNTAX_ERROR, "empty query string", null));
    }

    final String hashCode = Hashing.sha256().hashString(queryString, StandardCharsets.UTF_8).toString();

    final GQLDocument parsed;

    try {
      parsed = this.docs.parse(queryString);
    }
    catch (final GQLException ex) {
      return ZuluCompileResult.withErrors(
          new ZuluWarning.ParseWarning(ZuluWarningKind.SYNTAX_ERROR, queryString, ex));
    }

    final AtomicBoolean error = new AtomicBoolean(false);

    final GQLPreparedDocument doc = this.docs.prepareDocument(parsed);
    final ArrayList<ZuluWarning> errors = new ArrayList<>();

    class CollectingListener implements GQLPreparedValidationListener {

      @Override
      public void error(final ZField field, final GQLSourceRange location, final String string) {

        errors.add(new ZuluWarning() {

          @Override
          public ZuluWarningKind warningKind() {
            return ZuluWarningKind.UNDEFINED_VARIABLE;
          }

          @Override
          public GQLSourceLocation sourceLocation() {
            return location.start();
          }

          @Override
          public GQLPreparedSelection selection() {
            return null;
          }

          @Override
          public String detail() {
            return string;
          }

          @Override
          public ExecutableElement context() {
            return null;
          }

          @Override
          public Throwable cause() {
            return null;
          }

        });

        error.set(true);
      }

    }

    doc.validate(new CollectingListener());

    if (error.get()) {
      final ZuluCompileResult compileResult = ZuluCompileResult.withErrors(errors.toArray(new ZuluWarning[0]));
      this.cache.put(hashCode, compileResult);
      return compileResult;
    }

    final GQLPreparedOperation op = doc.operation(operationName).orElse(null);

    if (op == null) {

      if (operationName == null) {

        return ZuluCompileResult.withErrors(
            doc,
            new ZuluWarning.DocumentWarning(ZuluWarningKind.OPERATION_NAME_REQUIRED, doc));

      }

      return ZuluCompileResult.withErrors(
          doc,
          new ZuluWarning.DocumentWarning(ZuluWarningKind.INVALID_OPERATION, doc, operationName));

    }

    final ZuluCompileResult compileResult = this.compile(op);

    this.cache.put(hashCode, compileResult);

    return compileResult;

  }

  public ExecutableSchema schema() {
    return this.schema;
  }

  /**
   * returns a ZValue in a form it can be bound to.
   *
   * @param param
   *
   * @param value
   * @return
   */

  public Object get(final ExecutableInput param, final ZValue value) {
    final Class<?> rawType = param.fieldType().javaType().getRawType();
    switch (value.valueType().typeKind()) {
      case SCALAR: {
        final ZScalarValue scalar = (ZScalarValue) value;
        switch (scalar.valueType().baseType()) {
          case BOOLEAN:
            return ((ZBoolValue) scalar).boolValue();
          case DOUBLE:
            return ((ZDoubleValue) scalar).toString();
          case INT:
            return ((ZIntValue) scalar).intValue();
          case STRING: {
            final String stringValue = ((ZStringValue) scalar).stringValue();
            if (rawType.isEnum()) {
              return Arrays.stream(rawType.getEnumConstants())
                  .map(e -> Enum.class.cast(e))
                  .filter(e -> e.name().equals(stringValue))
                  .findFirst()
                  .get();
            }
            return stringValue;
          }
          default:
            break;
        }
        throw new IllegalArgumentException(scalar.valueType().baseType().toString());
      }
      case ARRAY:
        return ((ZArrayValue) value)
            .values()
            .map(val -> this.get(param, val))
            .toArray(length -> JavaExecutableUtils.makeArray(param, length));
      case ENUM:
      case STRUCT:
      case TUPLE:
      case VOID:
      default:
        break;
    }
    throw new IllegalArgumentException(value.valueType().typeKind().toString());
  }

  /**
   * true if a value of 'from' can be converted to 'to'
   *
   * @param from
   * @param to
   * @return
   */

  public boolean compatible(final ZTypeUse from, final ExecutableTypeUse to) {
    // TODO Auto-generated method stub
    return true;
  }

  /**
   * process this request.
   */

  public ZuluExecutionResult[] processRequestBatch(final ImmutableZuluServerRequest req) {

    final ZuluExecutionScope scope = new ZuluExecutionScope(this, req.injector());

    try {

      final ZuluExecutionResult[] res = new ZuluExecutionResult[req.queries().size()];

      for (int i = 0; i < req.queries().size(); ++i) {
        final ImmutableQuery q = req.queries().get(i);
        res[i] = this.processRequest(req, q, scope);
      }

      scope.complete(res);

      return res;

    }
    catch (final Throwable t) {

      scope.error(t);
      throw t;

    }

  }

  /**
   * use bind() instead.
   *
   * @param req
   * @param q
   * @param scope
   * @return
   *
   */
  @Deprecated()
  private ZuluExecutionResult processRequest(final ImmutableZuluServerRequest req, final ImmutableQuery q, final ZuluExecutionScope scope) {

    final ExecutionResult.Builder res = ExecutionResult.builder();

    final ZuluResultReceiver receiver = q.resultReceiver();

    // compile the query. may use cache if it already exists.
    final ZuluCompileResult compileResult = this.compile(q.query(), q.operationName(), q.persistedQuery());

    if (!compileResult.warnings().isEmpty()) {
      res.addAllNotes(compileResult.warnings());
    }

    final ZuluExecutable doc = compileResult.executable();

    if (doc == null) {
      // an error occured. messages will have been handled by the warnings.
      return res.build();
    }

    if (doc.outputType() == null) {
      res.addNote(new ZuluWarning.ParseWarning(ZuluWarningKind.INVALID_OPERATION, "unsupported operation type for this endpoint", null));
      return res.build();
    }

    final Stopwatch timer = Stopwatch.createStarted();

    Object instance;
    try {

      instance = req.injector().newInstance(compileResult.executable().javaType());

    }
    catch (final RuntimeException e) {
      throw e;
    }
    catch (final Throwable e) {
      throw new RuntimeException(e);
    }

    if (doc.executable().operationType() == GQLOpType.Subscription) {

      // subscriptions are handled separately - we first use the instance to set up the subscription
      // and then perform an execution on each value it returns. the main difference is the same field
      // may be returned multiple times.

      // flow control is important here - we may have a slow subscriber, and need to do some buffering and then
      // abort the subscription with a buffer overflow error if they can't keep up (or switch to on-disk spooling, etc).

      final ZuluSubscriptionContext sub = doc.executable().subscribe(instance, scope, new ZuluRequest(q.variables()));

      // then, subscribe to each event that is emitted by the subscription and execute against that.

    }
    else {

      // bind to the context for this caller.
      final ZuluContext ctx = doc.executable().bind(instance, scope);

      // execute
      final ZuluExecutionResult execres = ctx.execute(new ZuluRequest(q.variables()), receiver);

      // add the notes from execution to the response.
      res.addAllNotes(execres.notes());

    }

    // and execute it.
    try {
      return res.build();
    }
    finally {
      timer.stop();
      this.addTiming(timer);
    }

  }

  private void addTiming(final Stopwatch timer) {
    // todo
  }

  public static ZuluEngineBuilder builder() {
    return new ZuluEngineBuilder();
  }

  /**
   * new invocation API, which splits the internal binding from the actual fetching of the data in some scenarios
   * (currently just subscriptions). this allows flow controlled subscriptions. once the portal is returned, results can
   * be retrieved.
   */

  public ZuluPortal bind(final ImmutableBindParams q, final ZuluInjector injector) {

    final ZuluExecutionScope scope = new ZuluExecutionScope(this, injector);

    final ExecutionResult.Builder res = ExecutionResult.builder();

    // compile the query. may use cache if it already exists.
    final ZuluCompileResult compileResult = this.compile(q.query(), q.operationName(), q.persistedQuery());

    if (!compileResult.warnings().isEmpty()) {
      res.addAllNotes(compileResult.warnings());
    }

    final ZuluExecutable doc = compileResult.executable();

    if (doc == null) {
      // an error occured. messages will have been handled by the warnings.
      return new ErrorPortal(res.build());
    }

    if (doc.outputType() == null) {
      res.addNote(new ZuluWarning.ParseWarning(ZuluWarningKind.INVALID_OPERATION, "unsupported operation type for this endpoint", null));
      return new ErrorPortal(res.build());
    }

    final Stopwatch timer = Stopwatch.createStarted();

    try {

      Object instance;

      try {

        instance = injector.newInstance(compileResult.executable().javaType());

      }
      catch (final RuntimeException e) {
        throw e;
      }
      catch (final Throwable e) {
        throw new RuntimeException(e);
      }

      final ZuluRequest reqvars = new ZuluRequest(q.variables());

      final GQLOperationType optype = doc.executable().operationType();

      if (optype == GQLOpType.Query) {

        return new ImmediatePortal(doc.executable(), instance, scope, reqvars, res.build());

      }
      else if (optype == GQLOpType.Subscription) {

        return new SubscriptionPortal(doc.executable(), instance, scope, reqvars, res.build());

      }
      else if (optype == GQLOpType.Mutation) {

        // bind to the context for this caller.
        return new ImmediatePortal(doc.executable(), instance, scope, reqvars, res.build());

      }
      else {

        throw new IllegalArgumentException("invalid operation type: " + optype);

      }

    }
    finally {
      timer.stop();
      this.addTiming(timer);
    }

  }

}

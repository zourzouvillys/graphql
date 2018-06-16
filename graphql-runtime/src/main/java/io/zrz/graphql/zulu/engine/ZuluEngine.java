package io.zrz.graphql.zulu.engine;

import java.lang.reflect.Type;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.parser.GQLException;
import io.zrz.graphql.zulu.doc.CachingGQLDocumentManager;
import io.zrz.graphql.zulu.doc.GQLDocumentManager;
import io.zrz.graphql.zulu.doc.GQLPreparedDocument;
import io.zrz.graphql.zulu.doc.GQLPreparedOperation;
import io.zrz.graphql.zulu.executable.ExecutableInputField;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;
import io.zrz.graphql.zulu.server.ImmutableQuery;
import io.zrz.graphql.zulu.server.ImmutableZuluServerRequest;
import io.zrz.zulu.types.ZTypeUse;
import io.zrz.zulu.values.ZBoolValue;
import io.zrz.zulu.values.ZDoubleValue;
import io.zrz.zulu.values.ZIntValue;
import io.zrz.zulu.values.ZScalarValue;
import io.zrz.zulu.values.ZStringValue;
import io.zrz.zulu.values.ZValue;

public class ZuluEngine {

  private static Logger log = LoggerFactory.getLogger(ZuluEngine.class);

  private final ExecutableSchema schema;
  private final GQLDocumentManager docs;

  public ZuluEngine(Type queryRoot) {
    this(ExecutableSchema.builder().setRootType(GQLOpType.Query, queryRoot).build(false));
  }

  public ZuluEngine(ExecutableSchema schema) {
    this(schema, null);
  }

  public ZuluEngine(ExecutableSchema schema, GQLDocumentManager docmgr) {
    this.schema = Objects.requireNonNull(schema);

    if (docmgr == null)
      this.docs = new CachingGQLDocumentManager();
    else
      this.docs = Objects.requireNonNull(docmgr);

  }

  /**
   * execute a prepared operation.
   * 
   * @return
   */

  public ZuluCompileResult compile(GQLPreparedOperation op) {
    ExecutableBuilder builder = new ExecutableBuilder(this, op);
    return new ZuluCompileResult(op, builder, builder.build());
  }

  /**
   * execute a simple unnamed query.
   * 
   * @return
   */

  public ZuluCompileResult compile(String queryString) {
    return compile(docs.prepareDocument(docs.parse(queryString)).defaultOperation().get());
  }

  public ZuluCompileResult compile(String queryString, String operationName) {

    final GQLDocument parsed;

    try {
      parsed = docs.parse(queryString);
    }
    catch (GQLException ex) {
      return ZuluCompileResult.withErrors(
          new ZuluWarning.ParseWarning(ZuluWarningKind.SYNTAX_ERROR, queryString, ex));
    }

    GQLPreparedDocument doc = docs.prepareDocument(parsed);

    GQLPreparedOperation op = doc.operation(operationName).orElse(null);

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

    return compile(op);

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

  public Object get(ExecutableInputField param, ZValue value) {
    switch (value.valueType().typeKind()) {
      case SCALAR: {
        ZScalarValue scalar = (ZScalarValue) value;
        switch (scalar.valueType().baseType()) {
          case BOOLEAN:
            return ((ZBoolValue) scalar).boolValue();
          case DOUBLE:
            return ((ZDoubleValue) scalar).toString();
          case INT:
            return ((ZIntValue) scalar).intValue();
          case STRING:
            return ((ZStringValue) scalar).stringValue();
          default:
            break;
        }
        throw new IllegalArgumentException(scalar.valueType().baseType().toString());
      }
      case ARRAY:
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

  public boolean compatible(ZTypeUse from, ExecutableTypeUse to) {
    // TODO Auto-generated method stub
    return true;
  }

  /**
   * process this request.
   */

  public ZuluExecutionResult[] processRequestBatch(ImmutableZuluServerRequest req) {

    ZuluExecutionResult[] res = new ZuluExecutionResult[req.queries().size()];

    for (int i = 0; i < req.queries().size(); ++i) {
      ImmutableQuery q = req.queries().get(i);
      res[i] = processRequest(req, q);
    }

    return res;

  }

  private ZuluExecutionResult processRequest(ImmutableZuluServerRequest req, ImmutableQuery q) {

    ExecutionResult.Builder res = ExecutionResult.builder();

    ZuluResultReceiver receiver = q.resultReceiver();

    // compile the query. may use cache if it already exists.
    ZuluCompileResult compileResult = compile(q.query(), q.operationName());

    if (!compileResult.warnings().isEmpty()) {
      res.addAllNotes(compileResult.warnings());
    }

    ZuluExecutable doc = compileResult.executable();

    if (doc == null) {

      // an error occured. messages will have been handled by the warnings.
      return res.build();
    }

    Object instance;
    try {
      instance = compileResult.executable().javaType()
          .getRawType()
          .getDeclaredConstructor()
          .newInstance();
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Throwable e) {
      throw new RuntimeException(e);
    }

    // bind to the context for this caller.
    ZuluContext ctx = doc.executable().bind(instance);

    ZuluExecutionResult execres = ctx.execute(receiver);

    res.addAllNotes(execres.notes());

    // and execute it.
    return res.build();

  }

  public static ZuluEngineBuilder builder() {
    return new ZuluEngineBuilder();
  }

}

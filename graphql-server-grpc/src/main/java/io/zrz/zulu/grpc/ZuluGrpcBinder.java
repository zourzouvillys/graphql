package io.zrz.zulu.grpc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.doc.DefaultGQLPreparedOperation.OpInputType;
import io.zrz.graphql.zulu.engine.ZuluCompileResult;
import io.zrz.graphql.zulu.engine.ZuluContainerSelection;
import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.engine.ZuluExecutable;
import io.zrz.graphql.zulu.engine.ZuluExecutionResult;
import io.zrz.graphql.zulu.engine.ZuluRequest;
import io.zrz.graphql.zulu.engine.ZuluSelection;
import io.zrz.graphql.zulu.engine.ZuluWarning;
import io.zrz.graphql.zulu.executable.ExecutableScalarType;
import io.zrz.graphql.zulu.executable.ExecutableType;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;
import io.zrz.graphql.zulu.server.ImmutableQuery;
import io.zrz.graphql.zulu.server.ImmutableZuluServerRequest;
import io.zrz.zulu.graphql.GraphQLProtos;
import io.zrz.zulu.graphql.GraphQLProtos.ExecuteRequest;
import io.zrz.zulu.graphql.GraphQLProtos.FieldValueType;
import io.zrz.zulu.graphql.GraphQLProtos.InputParameter;
import io.zrz.zulu.graphql.GraphQLProtos.InputType;
import io.zrz.zulu.graphql.GraphQLProtos.Note;
import io.zrz.zulu.graphql.GraphQLProtos.OperationType;
import io.zrz.zulu.graphql.GraphQLProtos.PrepareReply;
import io.zrz.zulu.graphql.GraphQLProtos.PrepareReply.PreparedOperation;
import io.zrz.zulu.graphql.GraphQLProtos.PrepareRequest;
import io.zrz.zulu.graphql.GraphQLProtos.QueryReply;
import io.zrz.zulu.graphql.GraphQLProtos.QueryRequest;
import io.zrz.zulu.graphql.GraphQLProtos.ScalarType;
import io.zrz.zulu.graphql.GraphQLProtos.ScalarType.Builder;
import io.zrz.zulu.graphql.GraphQLProtos.ScalarTypeKind;
import io.zrz.zulu.graphql.GraphQLProtos.StructType;
import io.zrz.zulu.graphql.RxGraphQLGrpc;

public class ZuluGrpcBinder extends RxGraphQLGrpc.GraphQLImplBase {

  private ZuluEngine zulu;
  private Map<Integer, ZuluExecutable> prepared = new HashMap<>();
  private AtomicInteger alloc = new AtomicInteger();

  // private static Attributes.Key<Map<String, String>> PREPARED_DOCS = Attributes.Key.of("prepared_docs");

  public ZuluGrpcBinder(ZuluEngine zulu) {
    this.zulu = zulu;
  }

  public Flowable<PrepareReply> prepare(Single<PrepareRequest> request) {
    return request.flatMapPublisher(req -> prepare(req));
  }

  public Flowable<PrepareReply> prepare(PrepareRequest request) {

    try {

      PrepareReply.Builder reply = PrepareReply.newBuilder();

      zulu.compileDocument(request.getDocument())
          .forEach(op -> {

            // note: only valid within the same trsansport session. means that
            // connections must be direct. use with care!
            int id = alloc.incrementAndGet();

            PreparedOperation.Builder pop = PreparedOperation.newBuilder()
                .setOperationId(Integer.toHexString(id))
                .setOperationType(toOpType(op.executable().operationType()))
                .setOutputType(makeTypeSchema(op))
                .setInputType(makeInputType(op));

            op.warnings().stream().forEach(warn -> pop.addErrors(makeNote(warn)));

            op.executable().operationName().ifPresent(name -> pop.setOperationName(name));

            reply.addOperations(pop.build());

            prepared.put(id, op.executable());

          });

      return Flowable.just(reply.build());

    }
    catch (

    Exception ex) {
      ex.printStackTrace();
      return Flowable.error(ex);
    }
  }

  private Note makeNote(ZuluWarning warn) {
    return Note.newBuilder()
        .setMessage(warn.detail())
        .build();
  }

  private OperationType toOpType(GQLOperationType operationType) {
    switch (operationType.standardType().orElse(GQLOpType.Query)) {
      case Query:
        return OperationType.QUERY;
      case Mutation:
        return OperationType.MUTATION;
      case Subscription:
        return OperationType.SUBSCRIPTION;
      default:
        return OperationType.UNRECOGNIZED;
    }
  }

  private InputType makeInputType(ZuluCompileResult op) {

    OpInputType inputType = op.executable().inputType();

    InputType.Builder inb = InputType.newBuilder();

    inputType.fields().forEach((fieldName, field) -> {

      InputParameter.Builder fb = InputParameter.newBuilder();

      fb.setParameterName(fieldName);
      fb.setNullable(false);
      fb.setTypeName(field.fieldType().type().toString());

      inb.addParameters(fb);

    });

    return inb.build();

  }

  private StructType makeTypeSchema(ZuluCompileResult op) {
    AtomicInteger fidalloc = new AtomicInteger(0);
    AtomicInteger tidalloc = new AtomicInteger(0);
    StructType.Builder st = StructType.newBuilder();
    st.setTypeId(tidalloc.incrementAndGet());
    st.setTypeName(op.executable().outputType().typeName());
    makeTypeSchema(st, op.executable().selections(), fidalloc, tidalloc);
    return st.build();
  }

  private void makeTypeSchema(StructType.Builder st, ImmutableList<ZuluSelection> selections, AtomicInteger pidalloc, AtomicInteger tidalloc) {

    AtomicInteger fidalloc = new AtomicInteger(0);

    for (ZuluSelection field : selections) {

      ExecutableTypeUse fieldType = field.fieldType();

      switch (fieldType.logicalTypeKind()) {

        case OUTPUT:
        case INTERFACE: {
          ZuluContainerSelection ss = (ZuluContainerSelection) field;
          StructType.Builder nst = StructType.newBuilder();
          nst.setTypeName(ss.typeName());
          nst.setTypeId(tidalloc.incrementAndGet());
          makeTypeSchema(nst, ss.selections(), pidalloc, tidalloc);
          st.addFields(
              GraphQLProtos.Field.newBuilder()
                  .setOutputName(field.outputName())
                  .setFieldName(field.fieldName())
                  .setFieldType(
                      FieldValueType.newBuilder()
                          .setFieldId(fidalloc.incrementAndGet())
                          .setDimensions(field.fieldType().arity())
                          .setNullable(true)
                          .setStructType(nst)
                          .build()));
          break;
        }
        case ENUM:
        case SCALAR:
          st.addFields(
              GraphQLProtos.Field.newBuilder()
                  .setOutputName(field.outputName())
                  .setFieldName(field.fieldName())
                  .setFieldType(
                      FieldValueType.newBuilder()
                          .setFieldId(fidalloc.incrementAndGet())
                          .setPathId(pidalloc.incrementAndGet())
                          .setDimensions(field.fieldType().arity())
                          .setNullable(true)
                          .setScalarType(makeScalar(field.fieldType()))
                          .build()));
          break;
        case INPUT:
        case UNION:
        default:
          throw new IllegalArgumentException(fieldType.logicalTypeKind().name());
      }

    }

  }

  private ScalarType makeScalar(ExecutableTypeUse fieldType) {
    Builder sb = ScalarType.newBuilder();
    switch (fieldType.logicalType()) {
      case "Int":
        sb.setTypeKind(ScalarTypeKind.TYPE_INT);
        break;
      case "String":
        sb.setTypeKind(ScalarTypeKind.TYPE_STRING);
        break;
      case "Boolean":
        sb.setTypeKind(ScalarTypeKind.TYPE_BOOLEAN);
        break;
      case "Double":
        sb.setTypeKind(ScalarTypeKind.TYPE_DOUBLE);
        break;
      default:
        sb.setTypeName(fieldType.logicalType());
        sb.setTypeKind(typeKind(fieldType.type()));
        break;
    }
    return sb.build();

  }

  private ScalarTypeKind typeKind(ExecutableType executableType) {
    ExecutableScalarType st = (ExecutableScalarType) executableType;
    return ScalarTypeKind.TYPE_ANY;
  }

  public Flowable<QueryReply> execute(Single<ExecuteRequest> request) {
    return request.flatMapPublisher(req -> execute(req));
  }

  /**
   * executed a previously prepared operation.
   * 
   * @param req
   * @return
   */

  private Flowable<QueryReply> execute(ExecuteRequest req) {

    try {

      ZuluExecutable exec = this.prepared.get(Integer.parseInt(req.getOperationId(), 16));

      QueryReply.Builder qbr = QueryReply.newBuilder();

      GrpcResultReceiver results = new GrpcResultReceiver(qbr);

      exec.execute(new ZuluRequest(new GrpcParameterProvider(req.getVariables())), results);

      return Flowable.just(qbr.build());

    }
    catch (Exception ex) {
      ex.printStackTrace();
      return Flowable.error(ex);
    }

  }

  public Flowable<QueryReply> query(Single<QueryRequest> request) {
    return request.flatMapPublisher(req -> query(req));
  }

  public Flowable<QueryReply> query(QueryRequest param) {

    try {

      ImmutableZuluServerRequest.Builder b = ImmutableZuluServerRequest.builder();

      ImmutableQuery.Builder q = ImmutableQuery.builder();

      q.operationName(StringUtils.trimToNull(param.getOperationName()));
      q.query(param.getQuery());
      q.variables(new GrpcParameterProvider(param));

      QueryReply.Builder qbr = QueryReply.newBuilder();

      GrpcResultReceiver results = new GrpcResultReceiver(qbr);

      q.resultReceiver(results);

      //
      b.addQueries(q.build());

      ZuluExecutionResult[] status = zulu.processRequestBatch(b.build());

      if (!status[0].notes().isEmpty()) {

        for (ZuluWarning note : status[0].notes()) {
          qbr.addErrors(Note.newBuilder()
              .setMessage(note.detail())
              .build());

          if (note.cause() != null) {
            note.cause().printStackTrace();
          }

        }

      }

      return Flowable.just(qbr.build());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return Flowable.error(ex);
    }

    // throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
  }

}

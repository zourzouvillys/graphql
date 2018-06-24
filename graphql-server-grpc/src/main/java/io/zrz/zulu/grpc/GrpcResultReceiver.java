package io.zrz.zulu.grpc;

import java.util.Stack;

import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.protobuf.Value.Builder;
import com.google.protobuf.Value.KindCase;

import io.zrz.graphql.zulu.engine.ZuluContainerSelection;
import io.zrz.graphql.zulu.engine.ZuluExecutable;
import io.zrz.graphql.zulu.engine.ZuluResultReceiver;
import io.zrz.graphql.zulu.engine.ZuluSelection;
import io.zrz.graphql.zulu.engine.ZuluSelectionContainer;
import io.zrz.zulu.graphql.GraphQLProtos.QueryReply;

public class GrpcResultReceiver implements ZuluResultReceiver {

  private Stack<Value.Builder> state = new Stack<>();
  private QueryReply.Builder reply;

  public GrpcResultReceiver(QueryReply.Builder qbr) {
    this.reply = qbr;
  }

  @Override
  public void startStruct(ZuluSelectionContainer container, Object context) {

    this.state.push(Value.newBuilder().setStructValue(Struct.newBuilder()));

  }

  @Override
  public void endStruct(ZuluSelectionContainer container, Object context) {

    if (container instanceof ZuluExecutable) {

      Value.Builder stack = this.state.pop();

      this.reply.setData(stack);

      if (!state.isEmpty()) {
        throw new IllegalStateException("incorrect stack state");
      }

      return;

    }

    addToParent((ZuluSelection) container, context, this.state.pop());

  }

  @Override
  public void startList(ZuluSelection container, Object context) {
    state.push(Value.newBuilder().setListValue(ListValue.newBuilder()));
  }

  @Override
  public void endList(ZuluSelection container, Object context) {
    addToParent(container, context, state.pop());
  }

  public void addToParent(ZuluSelection container, Object context, Value.Builder value) {

    switch (state.peek().getKindCase()) {
      case STRUCT_VALUE:
        state.peek().getStructValueBuilder().putFields(container.outputName(), value.build());
        break;
      case LIST_VALUE:
        state.peek().getListValueBuilder().addValues(value.build());
        break;
      case BOOL_VALUE:
      case KIND_NOT_SET:
      case NULL_VALUE:
      case NUMBER_VALUE:
      case STRING_VALUE:
      default:
        throw new IllegalArgumentException();
    }

  }

  @Override
  public void push(ZuluSelectionContainer container, Object instance) {

  }

  @Override
  public void pop(ZuluSelectionContainer container, Object instance) {

  }

  @Override
  public void next(Object instance) {
  }

  public void add(ZuluSelection field, Value value) {
    switch (state.peek().getKindCase()) {
      case LIST_VALUE:
        state.peek().getListValueBuilder().addValues(value);
        break;
      case STRUCT_VALUE:
        state.peek().getStructValueBuilder().putFields(field.outputName(), value);
        break;
      default:
      case KIND_NOT_SET:
        throw new IllegalArgumentException(state.peek().getKindCase().name());
    }
  }

  @Override
  public void write(ZuluSelection field) {
    // nothign to do.
  }

  @Override
  public void write(ZuluSelection field, int value) {
    add(field, Value.newBuilder().setNumberValue(value).build());
  }

  @Override
  public void write(ZuluSelection field, long value) {
    add(field, Value.newBuilder().setNumberValue(value).build());
  }

  @Override
  public void write(ZuluSelection field, boolean value) {
    add(field, Value.newBuilder().setBoolValue(value).build());
  }

  @Override
  public void write(ZuluSelection field, double value) {
    add(field, Value.newBuilder().setNumberValue(value).build());
  }

  @Override
  public void write(ZuluSelection field, String value) {
    add(field, Value.newBuilder().setStringValue(value).build());
  }

  @Override
  public void write(ZuluSelection field, Object value) {
    add(field, Value.newBuilder().setStringValue(value.toString()).build());
  }

}

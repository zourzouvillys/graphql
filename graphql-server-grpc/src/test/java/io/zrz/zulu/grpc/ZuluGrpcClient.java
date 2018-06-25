package io.zrz.zulu.grpc;

import com.google.protobuf.Struct;

import io.grpc.ManagedChannel;
import io.reactivex.Flowable;
import io.zrz.zulu.graphql.GraphQLProtos.ExecuteRequest;
import io.zrz.zulu.graphql.GraphQLProtos.PrepareReply;
import io.zrz.zulu.graphql.GraphQLProtos.PrepareRequest;
import io.zrz.zulu.graphql.GraphQLProtos.QueryReply;
import io.zrz.zulu.graphql.GraphQLProtos.QueryRequest;
import io.zrz.zulu.graphql.RxGraphQLGrpc;
import io.zrz.zulu.graphql.RxGraphQLGrpc.RxGraphQLStub;

public class ZuluGrpcClient {

  private ManagedChannel ch;
  private RxGraphQLStub stub;

  public ZuluGrpcClient(ManagedChannel ch) {
    this.ch = ch;
    this.stub = RxGraphQLGrpc.newRxStub(ch);
  }

  public Flowable<QueryReply> query(String query) {

    return stub.query(QueryRequest.newBuilder()
        .setQuery(query)
        .build());

  }

  public Flowable<QueryReply> query(String query, Struct params) {

    return stub.query(QueryRequest.newBuilder()
        .setQuery(query)
        .setVariables(params)
        .build());

  }

  public Flowable<PrepareReply> prepare(String document) {
    return stub.prepare(PrepareRequest.newBuilder()
        .setDocument(document)
        .build());
  }

  public Flowable<QueryReply> execute(String operationId, Struct params) {
    return stub.execute(ExecuteRequest.newBuilder()
        .setOperationId(operationId)
        .setVariables(params)
        .build());
  }

}

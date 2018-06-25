package io.zrz.graphql.grpc.client;

import io.zrz.zulu.client.ZuluClient;
import io.zrz.zulu.client.ZuluQuery;
import io.zrz.zulu.client.ZuluResponse;

public class ZuluGrpcClient implements ZuluClient {

  @Override
  public <T extends ZuluResponse> ZuluQueryCall<T> query(final ZuluQuery<T> query) {
    return null;
  }

}

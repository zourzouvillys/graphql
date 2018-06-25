package io.zrz.zulu.client;

import io.zrz.graphql.grpc.client.ZuluQueryCall;

public interface ZuluClient {

  /**
   * performs a query, providing the response asynchronously.
   *
   * @param query
   * @return
   */

  <T extends ZuluResponse> ZuluQueryCall<T> query(ZuluQuery<T> query);

}

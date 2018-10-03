package io.zrz.zulu.server.netty;

import java.util.Optional;

public interface IncomingZuluRequest {

  /**
   * true if this request is an OPTIONS request.
   *
   * Zulu will not execute an OPTIONS request, but instead respond with http headers (including CORS if configured).
   *
   */

  boolean isOptions();

  /**
   * the origin of this request (e.g, the HTTP Origin header).
   */

  Optional<CharSequence> origin();

  /**
   * each of the operations in this request.
   */

  HttpOperationRequest request();

}

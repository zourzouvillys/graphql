package io.zrz.zulu.server.netty;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.AnnotatedElement;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.zrz.graphql.plugins.jackson.JacksonResultReceiver;
import io.zrz.graphql.plugins.jackson.ZuluJacksonParameterProvider;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.engine.ZuluExecutionResult;
import io.zrz.graphql.zulu.engine.ZuluWarning;
import io.zrz.graphql.zulu.engine.ZuluWarning.ExecutionError;
import io.zrz.graphql.zulu.executable.ExecutableType;
import io.zrz.graphql.zulu.server.ImmutableQuery;
import io.zrz.graphql.zulu.server.ImmutableZuluServerRequest;
import io.zrz.graphql.zulu.server.ZuluRequestProcessor;

public class ZuluHttpResponder implements HttpResponder {

  private static Logger log = LoggerFactory.getLogger(ZuluHttpResponder.class);

  private static ObjectMapper mapper = new ObjectMapper();

  private final ZuluEngine zulu;

  public ZuluHttpResponder(ZuluEngine zulu) {
    this.zulu = zulu;
  }

  /**
   * process the request.
   */

  @Override
  public FullHttpResponse processRequest(final FullHttpRequest request) {

    if (request.method().equals(HttpMethod.OPTIONS)) {

      final FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK);

      res.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);

      if (request.headers().getAsString(HttpHeaderNames.ORIGIN) != null) {
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, request.headers().getAsString(HttpHeaderNames.ORIGIN));
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS");
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Accept");
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, 3600);
      }

      return res;

    }

    FullHttpResponse res = handleRequest(request);

    if (request.headers().getAsString(HttpHeaderNames.ORIGIN) != null) {
      res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, request.headers().getAsString(HttpHeaderNames.ORIGIN));
      res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
      res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS");
      res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Accept");
      res.headers().add(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, 3600);
    }

    return res;

  }

  public FullHttpResponse handleRequest(final FullHttpRequest request) {

    try {
      log.debug("processing request {}", request.uri());

      QueryStringDecoder decoder = new QueryStringDecoder(request.uri());

      String path = decoder.path();

      if (request.method().equals(HttpMethod.GET)) {

        RequestParams params = new RequestParams();

        if (decoder.parameters().containsKey("query"))
          params.query = decoder.parameters().get("query").get(0);
        if (decoder.parameters().containsKey("operationName"))
          params.operationName = decoder.parameters().get("operationName").get(0);
        if (decoder.parameters().containsKey("variables"))
          params.variables = mapper.readValue(
              decoder.parameters().get("variables").get(0),
              mapper.getTypeFactory().constructMapType(Map.class, String.class, JsonNode.class));

        return process(path, new RequestParams[] { params }, HttpMethod.GET);

      }
      else if (request.method().equals(HttpMethod.POST)) {

        long contentLength = HttpUtil.getContentLength(request, 0L);

        CharSequence contentType = HttpUtil.getMimeType(request);
        CharSequence charset = HttpUtil.getCharsetAsSequence(request);

        RequestParams[] params = process(path, contentLength, contentType, charset, request.content());

        return process(path, params, HttpMethod.POST);

      }
      else {

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, METHOD_NOT_ALLOWED);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        return response;

      }

    }
    catch (Throwable ex) {

      log.error("error processing request", ex);

      DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
      response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
      return response;

    }

  }

  public static class RequestParams {
    @JsonProperty(required = false)
    public String query;
    @JsonProperty(required = false)
    public String operationName;
    @JsonProperty(required = false)
    public Map<String, JsonNode> variables;
  }

  private RequestParams[] process(String path, long contentLength, CharSequence contentType, CharSequence charset, ByteBuf byteBuf) {

    try (ByteBufInputStream in = new ByteBufInputStream(byteBuf, true)) {

      JsonNode tree = mapper.readTree(in);

      if (tree.isArray()) {
        return mapper.convertValue(tree, mapper.getTypeFactory().constructArrayType(RequestParams.class));
      }
      else if (tree.isObject()) {
        return new RequestParams[] { mapper.convertValue(tree, RequestParams.class) };
      }

    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    throw new IllegalArgumentException();

  }

  private ZuluRequestProcessor proc;

  /**
   * actually process the request.
   * 
   * @param path
   * @param params
   * @param get
   * @return
   */

  private FullHttpResponse process(String path, RequestParams[] params, HttpMethod method) throws Throwable {

    ImmutableZuluServerRequest.Builder b = ImmutableZuluServerRequest.builder();

    ByteBuf[] buffers = new ByteBuf[params.length];
    JacksonResultReceiver[] jgs = new JacksonResultReceiver[params.length];

    for (int i = 0; i < params.length; ++i) {

      RequestParams param = params[i];

      ImmutableQuery.Builder q = ImmutableQuery.builder();

      q.operationName(param.operationName);
      q.query(param.query);
      q.variables(new ZuluJacksonParameterProvider(param.variables));

      buffers[i] = Unpooled.buffer();

      ByteBufOutputStream os = new ByteBufOutputStream(buffers[i]);

      JsonGenerator jg = mapper.getFactory().createGenerator((OutputStream) os);
      jg.enable(Feature.AUTO_CLOSE_TARGET);

      jgs[i] = new JacksonResultReceiver(jg);

      q.resultReceiver(jgs[i]);

      //
      b.addQueries(q.build());

    }

    ZuluExecutionResult[] results = zulu.processRequestBatch(b.build());

    for (int i = 0; i < params.length; ++i) {
      jgs[i].generator().close();
    }

    // --

    ByteBuf buffer = Unpooled.buffer();

    ByteBufOutputStream os = new ByteBufOutputStream(buffer);

    JsonGenerator gen = mapper.getFactory().createGenerator((OutputStream) os);

    if (params.length > 1) {
      gen.writeStartArray();
    }

    for (int i = 0; i < params.length; ++i) {

      gen.writeStartObject();

      if (buffers[i].isReadable()) {
        gen.writeFieldName("data");
        gen.writeRawValue(new String(ByteBufUtil.getBytes(buffers[i]), StandardCharsets.UTF_8));
      }

      buffers[i].release();

      if (!results[i].notes().isEmpty()) {
        gen.writeArrayFieldStart("errors");
        results[i].notes().forEach(warn -> writeError(gen, warn));
        gen.writeEndArray();
      }

      gen.writeEndObject();

    }

    if (params.length > 1) {
      gen.writeEndArray();
    }

    gen.close();

    //
    int contentLength = buffer.readableBytes();

    final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, contentLength);
    return response;

  }

  private void writeError(JsonGenerator gen, ZuluWarning warn) {
    try {

      gen.writeStartObject();

      gen.writeStringField("message", warn.detail());
      gen.writeStringField("kind", warn.warningKind().name());
      gen.writeStringField("category", warn.warningKind().category().name());

      if (warn.sourceLocation() != null) {
        gen.writeArrayFieldStart("locations");
        gen.writeStartObject();
        gen.writeNumberField("line", warn.sourceLocation().lineNumber());
        gen.writeNumberField("column", warn.sourceLocation().lineOffset());
        gen.writeEndObject();
        gen.writeEndArray();
      }

      ExecutableType context = warn.context();

      if (context != null) {
        gen.writeObjectFieldStart("context");
        gen.writeStringField("typeName", context.typeName());
        gen.writeStringField("typeKind", context.logicalKind().name());

        if (warn instanceof ZuluWarning.OutputFieldWarning) {
          gen.writeStringField("fieldName", ((ZuluWarning.OutputFieldWarning) warn).element().fieldName());
        }
        if (warn instanceof ZuluWarning.ExecutionError) {
          ExecutionError exec = ((ZuluWarning.ExecutionError) warn);

          AnnotatedElement origin = exec.selection().origin().orElse(null);

          if (origin != null) {
            gen.writeStringField("origin", origin.toString());
          }

        }

        gen.writeEndObject();
      }

      Throwable cause = warn.cause();

      if (cause != null) {

        gen.writeObjectFieldStart("cause");

        gen.writeStringField("type", cause.getClass().getName());

        if (cause.getMessage() != null) {
          gen.writeStringField("message", cause.getMessage());
        }

        if (cause.getStackTrace() != null) {

          gen.writeArrayFieldStart("stack");

          for (StackTraceElement stack : cause.getStackTrace()) {

            if (StringUtils.equals(stack.getClassName(), getClass().getName())
                && StringUtils.equals(stack.getMethodName(), "process")) {
              break;
            }

            gen.writeStartObject();

            gen.writeStringField("class", stack.getClassName());
            gen.writeStringField("method", stack.getMethodName());

            if (stack.getLineNumber() != 0) {
              gen.writeNumberField("line", stack.getLineNumber());
            }

            if (stack.getFileName() != null) {
              gen.writeStringField("file", stack.getFileName());
            }

//            if (stack.getModuleName() != null) {
//              gen.writeStringField("module", stack.getModuleName());
//            }

            gen.writeEndObject();

          }

          gen.writeEndArray();

        }

        gen.writeEndObject();

      }

      GQLPreparedSelection sel = warn.selection();

      if (sel != null) {
        gen.writeStringField("path", sel.path());
      }

      gen.writeEndObject();
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}

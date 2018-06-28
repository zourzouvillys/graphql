package io.zrz.zulu.server.netty;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;

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
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.zrz.graphql.plugins.jackson.JacksonResultReceiver;
import io.zrz.graphql.plugins.jackson.ZuluJacksonParameterProvider;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.engine.ZuluExecutionResult;
import io.zrz.graphql.zulu.engine.ZuluWarning;
import io.zrz.graphql.zulu.engine.ZuluWarning.ExecutionError;
import io.zrz.graphql.zulu.executable.ExecutableElement;
import io.zrz.graphql.zulu.executable.ExecutableType;
import io.zrz.graphql.zulu.server.ImmutableQuery;
import io.zrz.graphql.zulu.server.ImmutableZuluServerRequest;
import io.zrz.graphql.zulu.server.ZuluInjector;
import io.zrz.graphql.zulu.server.ZuluRequestProcessor;

public class ZuluHttpResponder implements HttpResponder, ZuluInjector {

  private static Logger log = LoggerFactory.getLogger(ZuluHttpResponder.class);

  private static ObjectMapper mapper = new ObjectMapper();
  private final Map<TypeToken<?>, Object> instances = new HashMap<>();
  private final ZuluEngine zulu;

  public ZuluHttpResponder(final ZuluEngine zulu) {
    this.zulu = zulu;
  }

  /**
   * process the request.
   */

  @Override
  public FullHttpResponse processRequest(final FullHttpRequest request) {

    try {

      if (request.method().equals(HttpMethod.OPTIONS)) {

        final FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK);

        res.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);

        if (request.headers().getAsString(HttpHeaderNames.ORIGIN) != null) {
          res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
          res.headers().add(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, "content-type,ETag,Vary,Content-Encoding,Authorization");
          res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
          res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST,GET,OPTIONS");
          res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "content-type,Accept,if-none-match,Authorization");
          res.headers().add(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, 600);
        }

        return res;

      }

      final FullHttpResponse res = this.handleRequest(request);

      if (request.headers().getAsString(HttpHeaderNames.ORIGIN) != null) {
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Type,ETag,Vary,Content-Encoding,Authorization");
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS");
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "content-type,Accept,if-none-match,Authorization");
        res.headers().add(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, 600);
      }

      return res;

    }
    finally {

      if (request.refCnt() > 0) {
        request.release();
      }

    }

  }

  public FullHttpResponse handleRequest(final FullHttpRequest request) {

    try {

      log.trace("processing request {}", request.uri());

      final QueryStringDecoder decoder = new QueryStringDecoder(request.uri());

      final String path = decoder.path();

      if (request.method().equals(HttpMethod.GET)) {

        final RequestParams params = new RequestParams();

        if (decoder.parameters().containsKey("query"))
          params.query = decoder.parameters().get("query").get(0);

        if (decoder.parameters().containsKey("operationName"))
          params.operationName = decoder.parameters().get("operationName").get(0);

        if (decoder.parameters().containsKey("variables"))
          params.variables = mapper.readValue(
              decoder.parameters().get("variables").get(0),
              mapper.getTypeFactory().constructMapType(Map.class, String.class, JsonNode.class));

        if (decoder.parameters().containsKey("extensions"))
          params.extensions = mapper.readValue(
              decoder.parameters().get("extensions").get(0),
              mapper.getTypeFactory().constructMapType(Map.class, String.class, JsonNode.class));

        // If-None-Match
        final String ifNoneMatch = request.headers().get(HttpHeaderNames.IF_NONE_MATCH);

        return this.process(path, new RequestParams[] { params }, HttpMethod.GET, ifNoneMatch);

      }
      else if (request.method().equals(HttpMethod.POST)) {

        final long contentLength = HttpUtil.getContentLength(request, 0L);

        final CharSequence contentType = HttpUtil.getMimeType(request);
        final CharSequence charset = HttpUtil.getCharsetAsSequence(request);

        final RequestParams[] params = this.process(path, contentLength, contentType, charset, request.content());

        final String ifNoneMatch = request.headers().get(HttpHeaderNames.IF_NONE_MATCH);

        return this.process(path, params, HttpMethod.POST, ifNoneMatch);

      }
      else {

        final DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, METHOD_NOT_ALLOWED);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        return response;

      }

    }
    catch (final Throwable ex) {

      log.error("error processing request", ex);

      final DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
      response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
      return response;

    }

  }

  public static class RequestParams {
    @JsonProperty(required = false)
    public Map<String, JsonNode> extensions;
    @JsonProperty(required = false)
    public String query;
    @JsonProperty(required = false)
    public String operationName;
    @JsonProperty(required = false)
    public Map<String, JsonNode> variables;
  }

  private RequestParams[] process(
      final String path,
      final long contentLength,
      final CharSequence contentType,
      final CharSequence charset,
      final ByteBuf byteBuf) {

    try (ByteBufInputStream in = new ByteBufInputStream(byteBuf, true)) {

      final JsonNode tree = mapper.readTree(in);

      if (tree.isArray()) {
        return mapper.convertValue(tree, mapper.getTypeFactory().constructArrayType(RequestParams.class));
      }
      else if (tree.isObject()) {
        return new RequestParams[] { mapper.convertValue(tree, RequestParams.class) };
      }

    }
    catch (final IOException e) {
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
   * @param ifNoneMatch
   * @param get
   * @return
   */

  private FullHttpResponse process(final String path, final RequestParams[] params, final HttpMethod method, final String ifNoneMatch) throws Throwable {

    final ImmutableZuluServerRequest.Builder b = ImmutableZuluServerRequest.builder();

    b.injector(this);

    final ByteBuf[] buffers = new ByteBuf[params.length];
    final JacksonResultReceiver[] jgs = new JacksonResultReceiver[params.length];

    for (int i = 0; i < params.length; ++i) {

      final RequestParams param = params[i];

      final ImmutableQuery.Builder q = ImmutableQuery.builder();

      q.operationName(param.operationName);
      q.query(param.query);
      q.variables(new ZuluJacksonParameterProvider(param.variables));

      if (param.extensions != null && param.extensions.containsKey("persistedQuery")) {

        q.persistedQuery(param.extensions.get("persistedQuery").get("sha256Hash").asText());

      }

      buffers[i] = Unpooled.buffer();

      final ByteBufOutputStream os = new ByteBufOutputStream(buffers[i]);

      final JsonGenerator jg = mapper.getFactory().createGenerator((OutputStream) os);
      jg.enable(Feature.AUTO_CLOSE_TARGET);

      jgs[i] = new JacksonResultReceiver(jg);

      q.resultReceiver(jgs[i]);

      //
      b.addQueries(q.build());

    }

    final ZuluExecutionResult[] results = this.zulu.processRequestBatch(b.build());

    for (int i = 0; i < params.length; ++i) {
      jgs[i].generator().close();
    }

    // --

    final ByteBuf buffer = Unpooled.buffer();

    String dataHash = null;

    try (final ByteBufOutputStream os = new ByteBufOutputStream(buffer)) {

      final JsonGenerator gen = mapper.getFactory().createGenerator((OutputStream) os);

      if (params.length > 1) {
        gen.writeStartArray();
      }

      for (int i = 0; i < params.length; ++i) {

        gen.writeStartObject();

        if (buffers[i].isReadable()) {
          gen.writeFieldName("data");
          final String rawValue = new String(ByteBufUtil.getBytes(buffers[i]), StandardCharsets.UTF_8);
          gen.writeRawValue(rawValue);
        }

        buffers[i].release();

        if (!results[i].notes().isEmpty()) {
          gen.writeArrayFieldStart("errors");
          results[i].notes().forEach(warn -> this.writeError(gen, warn));
          gen.writeEndArray();
        }

        gen.writeEndObject();

      }

      if (params.length > 1) {
        gen.writeEndArray();
      }

      gen.close();

    }

    final Hasher hasher = Hashing.sha256().newHasher();

    buffer.forEachByte(proc -> {
      hasher.putByte(proc);
      return true;
    });

    dataHash = hasher.hash().toString();

    if (dataHash != null && ifNoneMatch != null) {

      if (StringUtils.equals(dataHash, ifNoneMatch)) {

        // no need for it ...
        buffer.release();

        final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);

        if (dataHash != null) {
          response.headers().set(HttpHeaderNames.ETAG, dataHash);
          response.headers().set(HttpHeaderNames.VARY, "Accept-Encoding");
        }

        return response;

      }

    }

    //
    final int contentLength = buffer.readableBytes();

    final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);

    if (dataHash != null && method == HttpMethod.GET) {
      response.headers().set(HttpHeaderNames.ETAG, dataHash);
      response.headers().set(HttpHeaderNames.VARY, "Accept-Encoding,Origin,Authorization");
    }

    response.headers().set(HttpHeaderNames.CONTENT_TYPE, MediaType.JSON_UTF_8);
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, contentLength);

    return response;

  }

  private void writeError(final JsonGenerator gen, final ZuluWarning warn) {
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

      final ExecutableElement context = warn.context();

      if (context != null) {

        gen.writeObjectFieldStart("context");

        if (context instanceof ExecutableType) {

          gen.writeStringField("typeName", ((ExecutableType) context).typeName());
          gen.writeStringField("typeKind", ((ExecutableType) context).logicalKind().name());

        }

        if (warn instanceof ZuluWarning.OutputFieldWarning) {
          gen.writeStringField("fieldName", ((ZuluWarning.OutputFieldWarning) warn).element().fieldName());
        }
        if (warn instanceof ZuluWarning.ExecutionError) {
          final ExecutionError exec = (ZuluWarning.ExecutionError) warn;

          final AnnotatedElement origin = exec.selection().origin().orElse(null);

          if (origin != null) {
            gen.writeStringField("origin", origin.toString());
          }

        }

        gen.writeEndObject();

      }

      final Throwable cause = warn.cause();

      if (cause != null) {

        gen.writeObjectFieldStart("cause");

        gen.writeStringField("type", cause.getClass().getName());

        if (cause.getMessage() != null) {
          gen.writeStringField("message", cause.getMessage());
        }

        if (cause.getStackTrace() != null) {

          gen.writeArrayFieldStart("stack");

          for (final StackTraceElement stack : cause.getStackTrace()) {

            if (StringUtils.equals(stack.getClassName(), this.getClass().getName())
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

            // if (stack.getModuleName() != null) {
            // gen.writeStringField("module", stack.getModuleName());
            // }

            gen.writeEndObject();

          }

          gen.writeEndArray();

        }

        gen.writeEndObject();

      }

      final GQLPreparedSelection sel = warn.selection();

      if (sel != null) {
        gen.writeStringField("path", sel.path());
      }

      gen.writeEndObject();
    }
    catch (final Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public <T> void bind(final TypeToken<?> type, final T instance) {
    this.instances.put(type, instance);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T newInstance(final TypeToken<T> javaType) {

    if (this.instances.containsKey(javaType)) {
      return (T) this.instances.get(javaType);
    }

    try {
      return (T) javaType
          .getRawType()
          .getDeclaredConstructor()
          .newInstance();
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
        | SecurityException e) {
      throw new RuntimeException(e);
    }

  }

}

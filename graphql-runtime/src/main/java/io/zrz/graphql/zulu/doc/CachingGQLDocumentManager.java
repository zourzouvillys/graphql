package io.zrz.graphql.zulu.doc;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.zulu.executable.ExecutableSchema;

/**
 * a document manager which caches input.
 *
 * @author theo
 *
 */

public class CachingGQLDocumentManager implements GQLDocumentManager {

  private static int DEFAULT_SIZE = 8192;

  private final GQLDocumentManager impl;

  private final LinkedHashMap<String, GQLPreparedDocument> cache = new LinkedHashMap<String, GQLPreparedDocument>(DEFAULT_SIZE) {

    private static final long serialVersionUID = 1L;

    @Override
    protected boolean removeEldestEntry(final Map.Entry<String, GQLPreparedDocument> eldest) {
      return this.size() > DEFAULT_SIZE;
    }

  };

  private ExecutableSchema schema;

  public CachingGQLDocumentManager() {
    this(new DefaultGQLDocumentManager());
  }

  public CachingGQLDocumentManager(final GQLDocumentManager impl) {
    this.impl = impl;
  }

  public CachingGQLDocumentManager(final ExecutableSchema schema) {
    this.schema = schema;
    this.impl = new DefaultGQLDocumentManager(schema);
  }

  @Override
  public GQLPreparedDocument prepareDocument(final String input) {
    return this.cache.computeIfAbsent(this.hash(input), _hash -> this.impl.prepareDocument(input));
  }

  @Override
  public GQLPreparedDocument prepareDocument(final GQLDocument doc) {
    return this.impl.prepareDocument(doc);
  }

  @Override
  public GQLDocument parse(final String input) {
    return this.impl.parse(input);
  }

  private String hash(final String input) {
    try {
      final MessageDigest md = MessageDigest.getInstance("MD5");
      final byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
      final BigInteger bigInt = new BigInteger(1, digest);
      String hashtext = bigInt.toString(16);
      // Now we need to zero pad it if you actually want the full 32 chars.
      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }
      return hashtext;
    }
    catch (final Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}

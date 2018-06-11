package io.zrz.graphql.core.runtime;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

import io.zrz.graphql.core.doc.GQLDocument;

/**
 * a document manager which caches input.
 * 
 * @author theo
 *
 */

public class CachingGQLDocumentManager implements GQLDocumentManager {

  private static int DEFAULT_SIZE = 8192;

  private GQLDocumentManager impl;

  private LinkedHashMap<String, GQLPreparedDocument> cache = new LinkedHashMap<>(DEFAULT_SIZE) {

    private static final long serialVersionUID = 1L;

    protected boolean removeEldestEntry(Map.Entry<String, GQLPreparedDocument> eldest) {
      return size() > DEFAULT_SIZE;
    }

  };

  public CachingGQLDocumentManager() {
    this(new DefaultGQLDocumentManager());
  }

  public CachingGQLDocumentManager(GQLDocumentManager impl) {
    this.impl = impl;
  }

  @Override
  public GQLPreparedDocument prepareDocument(final String input) {
    return cache.computeIfAbsent(hash(input), _hash -> impl.prepareDocument(input));
  }

  @Override
  public GQLPreparedDocument prepareDocument(GQLDocument doc) {
    return impl.prepareDocument(doc);
  }

  @Override
  public GQLDocument parse(String input) {
    return impl.parse(input);
  }

  private String hash(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
      BigInteger bigInt = new BigInteger(1, digest);
      String hashtext = bigInt.toString(16);
      // Now we need to zero pad it if you actually want the full 32 chars.
      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }
      return hashtext;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}

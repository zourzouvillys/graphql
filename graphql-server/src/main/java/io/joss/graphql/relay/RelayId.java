package io.joss.graphql.relay;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Splitter;

import io.joss.graphql.executor.ExecutorUtils;
import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * identifier for mapping type/id to a value we can provide to a user as a string.
 * 
 * it doesn't need to be secure or provide any sort of tampering support. IDs are just IDs. Invalid ones jsut get treated as a never
 * mathcing value.
 * 
 * @author theo
 *
 */

@ToString
@AllArgsConstructor
public class RelayId
{

  private String type;
  private String id;

  public static final RelayId fromParts(String type, long id)
  {
    return new RelayId(type, Long.toString(id));
  }

  public static final RelayId fromParts(String type, String id)
  {
    return new RelayId(type, id);
  }

  public static final RelayId fromString(String encoded)
  {
    List<String> parts = Splitter.on(':').splitToList(encoded);
    if (parts.size() == 1)
    {
      return null;
    }
    return fromParts(parts.get(0), parts.get(1));
  }

  public String type()
  {
    return this.type;
  }

  public int intId()
  {
    return Integer.parseInt(id);
  }

  public long longId()
  {
    return Long.parseLong(id);
  }

  public String id()
  {
    return this.id;
  }

  public String encode()
  {
    Objects.requireNonNull(type);
    return String.format("%s:%s", type, id);
  }

  public static String toString(String type, long id)
  {
    return fromParts(type, id).encode();
  }

  public static String toString(Class<?> type, long id)
  {
    return fromParts(ExecutorUtils.getGQLTypeName(type), id).encode();
  }

  public static String toString(String type, String id)
  {
    return fromParts(type, id).encode();
  }

  public static String toString(Class<?> type, String id)
  {
    return fromParts(ExecutorUtils.getGQLTypeName(type), id).encode();
  }

  /**
   * Paese the given relayId, returning the integer value of the token.
   * 
   * Throws {@link IllegalArgumentException} if the type doesn't match.
   * 
   * @param klass
   * @param relayId
   * @return
   */

  public static int parseInt(Class<?> klass, String relayId)
  {

    String type = ExecutorUtils.getGQLTypeName(klass);

    RelayId id = fromString(relayId);

    if (!id.type().equals(type))
    {
      throw new IllegalArgumentException("Invalid ID for type");
    }

    return id.intId();

  }

  public static long parseLong(Class<?> klass, String relayId)
  {

    String type = ExecutorUtils.getGQLTypeName(klass);

    RelayId id = fromString(relayId);

    if (!id.type().equals(type))
    {
      throw new IllegalArgumentException("Invalid ID for type: " + relayId);
    }

    return id.longId();

  }

}

package io.zrz.graphql.relay;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Splitter;

import io.zrz.graphql.core.binder.annotatons.GQLNonNull;
import io.zrz.graphql.executor.ExecutorUtils;
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

  private String id;

  public static final RelayId fromString(String encoded)
  {
    return new RelayId(encoded);
  }

  public String id()
  {
    return this.id;
  }

  public String encode()
  {
    return id;
  }

  public static @GQLNonNull String toString(Class<?> type, String id)
  {
    return id;
  }

  public static @GQLNonNull String toString(String type, String id)
  {
    return id;
  }

}

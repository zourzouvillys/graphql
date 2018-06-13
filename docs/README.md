# 


## Inheritance

methods defined in supertypes which are not defined in the type being exported will not be included by default so only
methods annotated with @GQLOutputField in those base types will be included.

to include methods from a supertype like the currently scanned class, annotate extends/implements with @GQLMixin.

```

public interface AdminUser extends @GQLMixin User {

 // ...

}


```

## Type Support

```
import java.util.Set;
import java.util.concurrent.Flow;

import com.google.common.collect.ImmutableSet;

import io.reactivex.Flowable;
import io.reactivex.Observable;



      String.class,

      Long.TYPE,
      Long.class,
      Integer.TYPE,
      Integer.class,
      Double.TYPE,
      Double.class,
      Boolean.TYPE,
      Boolean.class,

      //
      BigInteger.class,
      BigDecimal.class


      //
      java.util.Optional.class,
      com.google.common.base.Optional.class


      // eclipse JDT
      NonNull.class,
      Nullable.class


```
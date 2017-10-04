package io.zrz.graphql.core.schema.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.schema.InputUnit;

public class TypeBuilder {

  private final Model model;
  private final Map<String, AbstractType> registered = new HashMap<>();
  private final Map<String, Set<GQLTypeDeclaration>> inputs;
  private final Stack<String> current = new Stack<>();

  public TypeBuilder(Model model, List<InputUnit> inputs) {
    this.model = model;
    this.inputs = inputs.stream()
        .flatMap(in -> in.types().entrySet().stream())
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), this::merge));
  }

  /**
   * returns a reference to a type. note that this may not be fully constructed,
   * and should not be expected to be complete.
   *
   * needed to handle loops in type references.
   */

  <T extends Type> T lookup(String name) {

    final AbstractType ref = this.registered.get(name);

    if (ref == null) {

      // register on demand

      final Set<GQLTypeDeclaration> register = this.inputs.get(name);

      if (register == null) {
        throw new TypeRefNotFoundException(name, this.current);
      }

      return (T) this.build(name, register);

    }
    return (T) ref;
  }

  /**
   * called by the constructor of the type, so we can access the reference to it
   * while it is being constructed - otherwise we can't have an immutable type
   * with reference type loops.
   *
   * @param type
   * @param name
   */

  void register(AbstractType type, String name) {
    if (this.registered.containsKey(name)) {
      // shouldn't ever happen, as we check duplicates on registration
      throw new RuntimeException(name);
    }
    this.registered.put(name, type);
  }

  /**
   *
   * @param name
   * @param decls
   * @return
   * @return
   */

  AbstractType build(String name, Set<GQLTypeDeclaration> decls) {
    Preconditions.checkArgument(!decls.isEmpty());
    final TypeConstructor builder = new TypeConstructor(this, this.model, name);
    decls.forEach(in -> in.apply(builder));
    try {
      this.current.push(name);
      return builder.build();
    } finally {
      this.current.pop();

    }
  }

  /**
   * merge when we have a conflict.
   *
   * @param a
   * @param b
   * @return
   */

  private Set<GQLTypeDeclaration> merge(Set<GQLTypeDeclaration> a, Set<GQLTypeDeclaration> b) {
    return Stream.concat(a.stream(), b.stream())
        .collect(Collectors.toSet());
  }

  Map<String, ? extends Type> build() {

    while (!this.inputs.isEmpty()) {

      final Iterator<Entry<String, Set<GQLTypeDeclaration>>> it = this.inputs.entrySet().iterator();

      final Entry<String, Set<GQLTypeDeclaration>> next = it.next();

      if (!this.registered.containsKey(next.getKey())) {

        // note: registered in call to #register()
        this.build(next.getKey(), next.getValue());

      } else {

        // was registered eagerly because of reference

      }

      it.remove();

    }

    return this.registered;
  }

  public Model getModel() {
    return this.model;
  }

}

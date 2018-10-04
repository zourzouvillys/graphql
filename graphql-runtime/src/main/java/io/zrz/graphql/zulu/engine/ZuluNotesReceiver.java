package io.zrz.graphql.zulu.engine;

@FunctionalInterface
public interface ZuluNotesReceiver {

  void add(ZuluWarning note);

  default void addAll(final Iterable<ZuluWarning> notes) {
    notes.forEach(this::add);
  }

}

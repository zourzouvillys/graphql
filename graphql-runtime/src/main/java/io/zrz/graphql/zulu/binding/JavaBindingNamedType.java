package io.zrz.graphql.zulu.binding;

public class JavaBindingNamedType {

  private JavaBindingType type;
  private String name;
  private boolean iface;

  public JavaBindingNamedType(JavaBindingType type, String name, boolean iface) {
    this.type = type;
    this.name = name;
    this.iface = iface;
  }

  public boolean isInterface() {
    return this.iface;
  }

  public JavaBindingType type() {
    return this.type;
  }

  public String name() {
    return this.name;
  }

}

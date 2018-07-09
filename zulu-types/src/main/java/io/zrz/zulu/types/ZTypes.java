package io.zrz.zulu.types;

public enum ZTypes implements ZType {

  VOID {

    @Override
    public ZTypeKind typeKind() {
      return ZTypeKind.VOID;
    }

  }

}

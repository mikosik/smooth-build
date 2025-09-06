package org.smoothbuild.virtualmachine.bytecode.kind.base;

public enum KindId {
  BLOB,
  BOOL,
  INT,
  STRING,
  ARRAY,
  TUPLE,
  LAMBDA,
  INVOKE,
  ORDER,
  COMBINE,
  SELECT,
  CALL,
  PICK,
  IF,
  REFERENCE,
  MAP,
  CHOOSE,
  CHOICE,
  SWITCH,
  FOLD,
  ;

  @SuppressWarnings("EnumOrdinal")
  public byte byteMarker() {
    return (byte) ordinal();
  }

  private static final KindId[] VALUES = values();

  public static KindId fromOrdinal(int ordinal) {
    if (0 <= ordinal && ordinal < VALUES.length) {
      return VALUES[ordinal];
    }
    return null;
  }
}

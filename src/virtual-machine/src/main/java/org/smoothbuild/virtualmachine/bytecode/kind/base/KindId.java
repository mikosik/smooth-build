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
  ;

  public byte byteMarker() {
    return (byte) ordinal();
  }

  private static final KindId[] values = values();

  public static KindId fromOrdinal(int ordinal) {
    if (0 <= ordinal && ordinal < values.length) {
      return values[ordinal];
    }
    return null;
  }
}

package org.smoothbuild.virtualmachine.bytecode.kind.base;

public enum KindId {
  BLOB,
  BOOL,
  INT,
  STRING,
  ARRAY,
  TUPLE,
  LAMBDA,
  NATIVE_FUNC,
  ORDER,
  COMBINE,
  SELECT,
  CALL,
  PICK,
  IF,
  REFERENCE,
  MAP_FUNC,
  FUNC,
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

package org.smoothbuild.virtualmachine.bytecode.type;

public enum CategoryId {
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
  IF_FUNC,
  REFERENCE,
  MAP_FUNC,
  FUNC,
  ;

  public byte byteMarker() {
    return (byte) ordinal();
  }

  private static final CategoryId[] values = values();

  public static CategoryId fromOrdinal(int ordinal) {
    if (0 <= ordinal && ordinal < values.length) {
      return values[ordinal];
    }
    return null;
  }
}

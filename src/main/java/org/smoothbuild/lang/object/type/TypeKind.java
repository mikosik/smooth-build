package org.smoothbuild.lang.object.type;

public enum TypeKind {
  TYPE((byte) 0),
  NOTHING((byte) 1),
  TUPLE((byte) 2),
  ARRAY((byte) 3),
  BLOB((byte) 4),
  BOOL((byte) 5),
  STRING((byte) 6);

  private final byte marker;

  TypeKind(byte marker) {
    this.marker = marker;
  }

  public static TypeKind typeKindMarkedWith(byte marker) {
    for (TypeKind value : values()) {
      if (value.marker == marker) {
        return value;
      }
    }
    return null;
  }

  public byte marker() {
    return marker;
  }
}

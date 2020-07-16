package org.smoothbuild.lang.object.type;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.Nothing;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;

public enum TypeKind {
  TYPE((byte) 0, BinaryType.class),
  NOTHING((byte) 1, Nothing.class),
  TUPLE((byte) 2, Struct.class),
  ARRAY((byte) 3, Array.class),
  BLOB((byte) 4, Blob.class),
  BOOL((byte) 5, Bool.class),
  STRING((byte) 6, SString.class);

  private final byte marker;
  private final Class<? extends SObject> jType;

  TypeKind(byte marker, Class<? extends SObject> jType) {
    this.marker = marker;
    this.jType = jType;
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

  public Class<? extends SObject> jType() {
    return jType;
  }
}

package org.smoothbuild.db.object.spec;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Call;
import org.smoothbuild.db.object.base.Const;
import org.smoothbuild.db.object.base.EArray;
import org.smoothbuild.db.object.base.FieldRead;
import org.smoothbuild.db.object.base.Int;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;

import com.google.common.collect.ImmutableMap;

public enum SpecKind {
  // Obj-s
  INT((byte) 0, Int.class),
  NOTHING((byte) 1, null),
  TUPLE((byte) 2, Tuple.class),
  ARRAY((byte) 3, Array.class),
  BLOB((byte) 4, Blob.class),
  BOOL((byte) 5, Bool.class),
  STRING((byte) 6, Str.class),
  // Expr-s
  CONST((byte) 7, Const.class),
  FIELD_READ((byte) 8, FieldRead.class),
  CALL((byte) 9, Call.class),
  EARRAY((byte) 10, EArray.class);

  private static final ImmutableMap<Byte, SpecKind> markerToSpecKindMap =
      ImmutableMap.<Byte, SpecKind>builder()
          .put((byte) 0, INT)
          .put((byte) 1, NOTHING)
          .put((byte) 2, TUPLE)
          .put((byte) 3, ARRAY)
          .put((byte) 4, BLOB)
          .put((byte) 5, BOOL)
          .put((byte) 6, STRING)
          .put((byte) 7, CONST)
          .put((byte) 8, FIELD_READ)
          .put((byte) 9, CALL)
          .put((byte) 10, EARRAY)
          .build();

  private final byte marker;
  private final Class<? extends Obj> jType;

  SpecKind(byte marker, Class<? extends Obj> jType) {
    this.marker = marker;
    this.jType = jType;
  }

  public static SpecKind fromMarker(byte marker) {
    return markerToSpecKindMap.get(marker);
  }

  public byte marker() {
    return marker;
  }

  public Class<? extends Obj> jType() {
    return jType;
  }
}

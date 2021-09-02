package org.smoothbuild.db.object.spec.base;

import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.EArray;
import org.smoothbuild.db.object.obj.expr.FieldRead;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;

import com.google.common.collect.ImmutableMap;

public enum SpecKind {
  // Obj-s
  INT((byte) 0, Int.class),
  NOTHING((byte) 1, null),
  RECORD((byte) 2, Rec.class),
  ARRAY((byte) 3, Array.class),
  BLOB((byte) 4, Blob.class),
  BOOL((byte) 5, Bool.class),
  STRING((byte) 6, Str.class),
  // Expr-s
  CONST((byte) 7, Const.class),
  FIELD_READ((byte) 8, FieldRead.class),
  CALL((byte) 9, Call.class),
  EARRAY((byte) 10, EArray.class),
  NULL((byte) 11, Null.class);

  private static final ImmutableMap<Byte, SpecKind> markerToSpecKindMap =
      ImmutableMap.<Byte, SpecKind>builder()
          .put((byte) 0, INT)
          .put((byte) 1, NOTHING)
          .put((byte) 2, RECORD)
          .put((byte) 3, ARRAY)
          .put((byte) 4, BLOB)
          .put((byte) 5, BOOL)
          .put((byte) 6, STRING)
          .put((byte) 7, CONST)
          .put((byte) 8, FIELD_READ)
          .put((byte) 9, CALL)
          .put((byte) 10, EARRAY)
          .put((byte) 11, NULL)
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

package org.smoothbuild.db.object.spec.base;

import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.expr.ArrayExpr;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.obj.expr.RecExpr;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.DefinedLambda;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.NativeLambda;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;

import com.google.common.collect.ImmutableMap;

public enum SpecKind {
  // Obj-s
  ARRAY((byte) 0, Array.class),
  BLOB((byte) 1, Blob.class),
  BOOL((byte) 2, Bool.class),
  DEFINED_LAMBDA((byte) 3, DefinedLambda.class),
  INT((byte) 4, Int.class),
  NATIVE_LAMBDA((byte) 5, NativeLambda.class),
  NOTHING((byte) 6, null),
  RECORD((byte) 7, Rec.class),
  STRING((byte) 8, Str.class),

  // Expr-s
  CALL((byte) 9, Call.class),
  CONST((byte) 10, Const.class),
  ARRAY_EXPR((byte) 11, ArrayExpr.class),
  SELECT((byte) 12, Select.class),
  NULL((byte) 13, Null.class),
  REF((byte) 14, Ref.class),
  RECORD_EXPR((byte) 15, RecExpr.class),

  ABSENT((byte) 16, null),
  VARIABLE((byte) 17, null),
  ANY((byte) 18, null);

  private static final ImmutableMap<Byte, SpecKind> markerToSpecKindMap =
      ImmutableMap.<Byte, SpecKind>builder()
          .put((byte) 0, ARRAY)
          .put((byte) 1, BLOB)
          .put((byte) 2, BOOL)
          .put((byte) 3, DEFINED_LAMBDA)
          .put((byte) 4, INT)
          .put((byte) 5, NATIVE_LAMBDA)
          .put((byte) 6, NOTHING)
          .put((byte) 7, RECORD)
          .put((byte) 8, STRING)

          .put((byte) 9, CALL)
          .put((byte) 10, CONST)
          .put((byte) 11, ARRAY_EXPR)
          .put((byte) 12, SELECT)
          .put((byte) 13, NULL)
          .put((byte) 14, REF)
          .put((byte) 15, RECORD_EXPR)
          .put((byte) 16, ABSENT)
          .put((byte) 17, VARIABLE)
          .put((byte) 18, ANY)
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

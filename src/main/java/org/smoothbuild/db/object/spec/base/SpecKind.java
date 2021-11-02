package org.smoothbuild.db.object.spec.base;

import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Construct;
import org.smoothbuild.db.object.obj.expr.Order;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.expr.StructExpr;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.obj.val.Tuple;

import com.google.common.collect.ImmutableMap;

public enum SpecKind {
  // Obj-s
  ARRAY((byte) 0, Array.class),
  BLOB((byte) 1, Blob.class),
  BOOL((byte) 2, Bool.class),
  LAMBDA((byte) 3, Lambda.class),
  INT((byte) 4, Int.class),
  STRUCT((byte) 5, Struc_.class),
  NOTHING((byte) 6, null),
  TUPLE((byte) 7, Tuple.class),
  STRING((byte) 8, Str.class),

  // Expr-s
  CALL((byte) 9, Call.class),
  CONST((byte) 10, Const.class),
  ORDER((byte) 11, Order.class),
  SELECT((byte) 12, Select.class),
  REF((byte) 14, Ref.class),
  CONSTRUCT((byte) 15, Construct.class),

  VARIABLE((byte) 17, null),
  ANY((byte) 18, null),
  NATIVE_METHOD((byte) 19, null),
  INVOKE((byte) 20, null),
  STRUCT_EXPR((byte) 21, StructExpr.class);

  private static final ImmutableMap<Byte, SpecKind> markerToSpecKindMap =
      ImmutableMap.<Byte, SpecKind>builder()
          .put((byte) 0, ARRAY)
          .put((byte) 1, BLOB)
          .put((byte) 2, BOOL)
          .put((byte) 3, LAMBDA)
          .put((byte) 4, INT)
          .put((byte) 5, STRUCT)
          .put((byte) 6, NOTHING)
          .put((byte) 7, TUPLE)
          .put((byte) 8, STRING)

          .put((byte) 9, CALL)
          .put((byte) 10, CONST)
          .put((byte) 11, ORDER)
          .put((byte) 12, SELECT)
          .put((byte) 14, REF)
          .put((byte) 15, CONSTRUCT)
          .put((byte) 17, VARIABLE)
          .put((byte) 18, ANY)
          .put((byte) 19, NATIVE_METHOD)
          .put((byte) 20, INVOKE)
          .put((byte) 21, STRUCT_EXPR)
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

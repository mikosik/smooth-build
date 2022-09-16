package org.smoothbuild.bytecode.type;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.IfB;
import org.smoothbuild.bytecode.expr.oper.InvokeB;
import org.smoothbuild.bytecode.expr.oper.MapB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.ParamRefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.MethodB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;

import com.google.common.collect.ImmutableMap;

public enum CatKindB {
  // @formatter:off
  BLOB(           (byte) 0,  BlobB.class),
  BOOL(           (byte) 1,  BoolB.class),
  INT(            (byte) 2,  IntB.class),
  STRING(         (byte) 3,  StringB.class),

  ARRAY(          (byte) 4,  ArrayB.class),
  TUPLE(          (byte) 5,  TupleB.class),

  FUNC(           (byte) 6, FuncB.class),
  METHOD(         (byte) 7,  MethodB.class),

  ORDER(          (byte) 8, OrderB.class),
  COMBINE(        (byte) 9, CombineB.class),
  SELECT(         (byte) 10, SelectB.class),
  CALL(           (byte) 11,  CallB.class),
  INVOKE(         (byte) 12, InvokeB.class),
  IF(             (byte) 13,  IfB.class),
  PARAM_REF(      (byte) 14, ParamRefB.class),
  MAP(            (byte) 15, MapB.class),
  ;
  // @formatter:on

  private static final ImmutableMap<Byte, CatKindB> markerToObjKindMap =
      ImmutableMap.<Byte, CatKindB>builder()
          .put((byte) 0, BLOB)
          .put((byte) 1, BOOL)
          .put((byte) 2, INT)
          .put((byte) 3, STRING)
          .put((byte) 4, ARRAY)
          .put((byte) 5, TUPLE)
          .put((byte) 6, FUNC)
          .put((byte) 7, METHOD)
          .put((byte) 8, ORDER)
          .put((byte) 9, COMBINE)
          .put((byte) 10, SELECT)
          .put((byte) 11, CALL)
          .put((byte) 12, INVOKE)
          .put((byte) 13, IF)
          .put((byte) 14, PARAM_REF)
          .put((byte) 15, MAP)
          .build();

  private final byte marker;
  private final Class<? extends ExprB> typeJ;

  CatKindB(byte marker, Class<? extends ExprB> typeJ) {
    this.marker = marker;
    this.typeJ = typeJ;
  }

  public static CatKindB fromMarker(byte marker) {
    return markerToObjKindMap.get(marker);
  }

  public byte marker() {
    return marker;
  }

  public Class<? extends ExprB> typeJ() {
    return typeJ;
  }
}

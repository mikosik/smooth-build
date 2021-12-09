package org.smoothbuild.db.object.type.base;

import java.util.function.BiFunction;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.IfH;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MethodH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.obj.val.ValH;

import com.google.common.collect.ImmutableMap;

public enum CatKindH {
  // @formatter:off
  ARRAY(        (byte) 0,  ArrayH.class,      ArrayH::new),
  BLOB(         (byte) 1,  BlobH.class,       BlobH::new),
  BOOL(         (byte) 2,  BoolH.class,       BoolH::new),
  METHOD(       (byte) 3,  MethodH.class,     MethodH::new),
  INT(          (byte) 4,  IntH.class,        IntH::new),
  IF(           (byte) 5,  IfH.class,         IfH::new),
  NOTHING(      (byte) 6,  ValH.class,        CatKindH::throwException),
  TUPLE(        (byte) 7,  TupleH.class,      TupleH::new),
  STRING(       (byte) 8,  StringH.class,     StringH::new),
  CALL(         (byte) 9,  CallH.class,       CallH::new),
  FUNC(         (byte) 10, FuncH.class,       FuncH::new),
  ORDER(        (byte) 11, OrderH.class,      OrderH::new),
  SELECT(       (byte) 12, SelectH.class,     SelectH::new),
  PARAM_REF(    (byte) 14, ParamRefH.class,   ParamRefH::new),
  COMBINE(      (byte) 15, CombineH.class,    CombineH::new),
  VARIABLE(     (byte) 17, ValH.class,        CatKindH::throwException),
  ANY(          (byte) 18, ValH.class,        CatKindH::throwException),
  INVOKE(       (byte) 19, InvokeH.class,     InvokeH::new),
  MAP(          (byte) 20, MapH.class,        MapH::new),
  ;
  // @formatter:on

  private static ObjH throwException(MerkleRoot merkleRoot, ObjDb objDb) {
    throw new UnsupportedOperationException();
  }

  private static final ImmutableMap<Byte, CatKindH> markerToObjKindMap =
      ImmutableMap.<Byte, CatKindH>builder()
          .put((byte) 0, ARRAY)
          .put((byte) 1, BLOB)
          .put((byte) 2, BOOL)
          .put((byte) 3, METHOD)
          .put((byte) 4, INT)
          .put((byte) 5, IF)
          .put((byte) 6, NOTHING)
          .put((byte) 7, TUPLE)
          .put((byte) 8, STRING)
          .put((byte) 9, CALL)
          .put((byte) 10, FUNC)
          .put((byte) 11, ORDER)
          .put((byte) 12, SELECT)
          .put((byte) 14, PARAM_REF)
          .put((byte) 15, COMBINE)
          .put((byte) 17, VARIABLE)
          .put((byte) 18, ANY)
          .put((byte) 19, INVOKE)
          .put((byte) 20, MAP)
          .build();

  private final byte marker;
  private final Class<? extends ObjH> typeJ;
  private final BiFunction<MerkleRoot, ObjDb, ObjH> instantiator;

  CatKindH(byte marker, Class<? extends ObjH> typeJ,
      BiFunction<MerkleRoot, ObjDb, ObjH> instantiator) {
    this.marker = marker;
    this.typeJ = typeJ;
    this.instantiator = instantiator;
  }

  public static CatKindH fromMarker(byte marker) {
    return markerToObjKindMap.get(marker);
  }

  public byte marker() {
    return marker;
  }

  public Class<? extends ObjH> typeJ() {
    return typeJ;
  }

  public ObjH newInstanceJ(MerkleRoot merkleRoot, ObjDb objDb) {
    return instantiator.apply(merkleRoot, objDb);
  }
}

package org.smoothbuild.db.bytecode.type.base;

import java.util.function.BiFunction;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.base.ObjB;
import org.smoothbuild.db.bytecode.obj.expr.CallB;
import org.smoothbuild.db.bytecode.obj.expr.CombineB;
import org.smoothbuild.db.bytecode.obj.expr.IfB;
import org.smoothbuild.db.bytecode.obj.expr.InvokeB;
import org.smoothbuild.db.bytecode.obj.expr.MapB;
import org.smoothbuild.db.bytecode.obj.expr.OrderB;
import org.smoothbuild.db.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.db.bytecode.obj.expr.SelectB;
import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.obj.val.BlobB;
import org.smoothbuild.db.bytecode.obj.val.BoolB;
import org.smoothbuild.db.bytecode.obj.val.FuncB;
import org.smoothbuild.db.bytecode.obj.val.IntB;
import org.smoothbuild.db.bytecode.obj.val.MethodB;
import org.smoothbuild.db.bytecode.obj.val.StringB;
import org.smoothbuild.db.bytecode.obj.val.TupleB;
import org.smoothbuild.db.bytecode.obj.val.ValB;

import com.google.common.collect.ImmutableMap;

public enum CatKindB {
  // @formatter:off
  ARRAY(        (byte) 0,  ArrayB.class,      ArrayB::new),
  BLOB(         (byte) 1,  BlobB.class,       BlobB::new),
  BOOL(         (byte) 2,  BoolB.class,       BoolB::new),
  METHOD(       (byte) 3,  MethodB.class,     MethodB::new),
  INT(          (byte) 4,  IntB.class,        IntB::new),
  IF(           (byte) 5,  IfB.class,         IfB::new),
  NOTHING(      (byte) 6,  ValB.class,        CatKindB::throwException),
  TUPLE(        (byte) 7,  TupleB.class,      TupleB::new),
  STRING(       (byte) 8,  StringB.class,     StringB::new),
  CALL(         (byte) 9,  CallB.class,       CallB::new),
  FUNC(         (byte) 10, FuncB.class,       FuncB::new),
  ORDER(        (byte) 11, OrderB.class,      OrderB::new),
  SELECT(       (byte) 12, SelectB.class,     SelectB::new),
  PARAM_REF(    (byte) 14, ParamRefB.class,   ParamRefB::new),
  COMBINE(      (byte) 15, CombineB.class,    CombineB::new),
  VARIABLE(     (byte) 17, ValB.class,        CatKindB::throwException),
  ANY(          (byte) 18, ValB.class,        CatKindB::throwException),
  INVOKE(       (byte) 19, InvokeB.class,     InvokeB::new),
  MAP(          (byte) 20, MapB.class,        MapB::new),
  ;
  // @formatter:on

  private static ObjB throwException(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    throw new UnsupportedOperationException();
  }

  private static final ImmutableMap<Byte, CatKindB> markerToObjKindMap =
      ImmutableMap.<Byte, CatKindB>builder()
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
  private final Class<? extends ObjB> typeJ;
  private final BiFunction<MerkleRoot, ByteDbImpl, ObjB> instantiator;

  CatKindB(byte marker, Class<? extends ObjB> typeJ,
      BiFunction<MerkleRoot, ByteDbImpl, ObjB> instantiator) {
    this.marker = marker;
    this.typeJ = typeJ;
    this.instantiator = instantiator;
  }

  public static CatKindB fromMarker(byte marker) {
    return markerToObjKindMap.get(marker);
  }

  public byte marker() {
    return marker;
  }

  public Class<? extends ObjB> typeJ() {
    return typeJ;
  }

  public ObjB newInstanceJ(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return instantiator.apply(merkleRoot, byteDb);
  }
}

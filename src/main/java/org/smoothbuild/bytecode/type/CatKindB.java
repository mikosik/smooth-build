package org.smoothbuild.bytecode.type;

import java.util.function.BiFunction;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
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
  ARRAY(          (byte) 0,  ArrayB.class,      ArrayB::new),
  BLOB(           (byte) 1,  BlobB.class,       BlobB::new),
  BOOL(           (byte) 2,  BoolB.class,       BoolB::new),
  METHOD(         (byte) 3,  MethodB.class,     MethodB::new),
  INT(            (byte) 4,  IntB.class,        IntB::new),
  IF(             (byte) 5,  IfB.class,         IfB::new),
  // TODO unused 6
  TUPLE(          (byte) 7,  TupleB.class,      TupleB::new),
  STRING(         (byte) 8,  StringB.class,     StringB::new),
  CALL(           (byte) 9,  CallB.class,       CallB::new),
  FUNC(           (byte) 10, FuncB.class,       FuncB::new),
  ORDER(          (byte) 11, OrderB.class,      OrderB::new),
  SELECT(         (byte) 12, SelectB.class,     SelectB::new),
  // TODO unused 13
  PARAM_REF(      (byte) 14, ParamRefB.class,   ParamRefB::new),
  COMBINE(        (byte) 15, CombineB.class,    CombineB::new),
  // TODO unused 16
  // TODO unused 17
  // TODO unused 18
  INVOKE(         (byte) 19, InvokeB.class,     InvokeB::new),
  MAP(            (byte) 20, MapB.class,        MapB::new),
  ;
  // @formatter:on

  private static ExprB throwException(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
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
          .put((byte) 7, TUPLE)
          .put((byte) 8, STRING)
          .put((byte) 9, CALL)
          .put((byte) 10, FUNC)
          .put((byte) 11, ORDER)
          .put((byte) 12, SELECT)
          .put((byte) 14, PARAM_REF)
          .put((byte) 15, COMBINE)
          .put((byte) 19, INVOKE)
          .put((byte) 20, MAP)
          .build();

  private final byte marker;
  private final Class<? extends ExprB> typeJ;
  private final BiFunction<MerkleRoot, BytecodeDb, ExprB> instantiator;

  CatKindB(byte marker, Class<? extends ExprB> typeJ,
      BiFunction<MerkleRoot, BytecodeDb, ExprB> instantiator) {
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

  public Class<? extends ExprB> typeJ() {
    return typeJ;
  }

  public ExprB newInstanceJ(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return instantiator.apply(merkleRoot, bytecodeDb);
  }
}

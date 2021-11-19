package org.smoothbuild.db.object.type.base;

import java.util.function.BiFunction;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.ConstH;
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.IfH;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NativeMethodH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;

import com.google.common.collect.ImmutableMap;

public enum TypeKindH {
  // Obj-s
  ARRAY((byte) 0, ArrayH.class, ArrayH::new),
  BLOB((byte) 1, BlobH.class, BlobH::new),
  BOOL((byte) 2, BoolH.class, BoolH::new),
  FUNCTION((byte) 3, FunctionH.class, FunctionH::new),
  INT((byte) 4, IntH.class, IntH::new),
  IF((byte) 5, IfH.class, IfH::new),
  NOTHING((byte) 6, null, TypeKindH::throwException),
  TUPLE((byte) 7, TupleH.class, TupleH::new),
  STRING((byte) 8, StringH.class, StringH::new),

  // Expr-s
  CALL((byte) 9, CallH.class, CallH::new),
  CONST((byte) 10, ConstH.class, ConstH::new),
  ORDER((byte) 11, OrderH.class, OrderH::new),
  SELECT((byte) 12, SelectH.class, SelectH::new),
  REF((byte) 14, RefH.class, RefH::new),
  CONSTRUCT((byte) 15, ConstructH.class, ConstructH::new),

  VARIABLE((byte) 17, null, TypeKindH::throwException),
  ANY((byte) 18, null, TypeKindH::throwException),
  NATIVE_METHOD((byte) 19, null, NativeMethodH::new),
  INVOKE((byte) 20, null, InvokeH::new),
  MAP((byte) 21, null, MapH::new);

  private static ObjectH throwException(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    throw new UnsupportedOperationException();
  }

  private static final ImmutableMap<Byte, TypeKindH> markerToObjKindMap =
      ImmutableMap.<Byte, TypeKindH>builder()
          .put((byte) 0, ARRAY)
          .put((byte) 1, BLOB)
          .put((byte) 2, BOOL)
          .put((byte) 3, FUNCTION)
          .put((byte) 4, INT)
          .put((byte) 5, IF)
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
          .put((byte) 21, MAP)
          .build();

  private final byte marker;
  private final Class<? extends ObjectH> jType;
  private final BiFunction<MerkleRoot, ObjectHDb, ObjectH> instantiator;

  TypeKindH(byte marker, Class<? extends ObjectH> jType,
      BiFunction<MerkleRoot, ObjectHDb, ObjectH> instantiator) {
    this.marker = marker;
    this.jType = jType;
    this.instantiator = instantiator;
  }

  public static TypeKindH fromMarker(byte marker) {
    return markerToObjKindMap.get(marker);
  }

  public byte marker() {
    return marker;
  }

  public Class<? extends ObjectH> jType() {
    return jType;
  }

  public ObjectH newInstanceJ(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return instantiator.apply(merkleRoot, objectHDb);
  }
}

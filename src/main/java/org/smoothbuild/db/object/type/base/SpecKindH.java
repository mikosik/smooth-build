package org.smoothbuild.db.object.type.base;

import java.util.function.BiFunction;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefinedFunctionH;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.obj.val.IfFunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MapFunctionH;
import org.smoothbuild.db.object.obj.val.NativeFunctionH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;

import com.google.common.collect.ImmutableMap;

public enum SpecKindH {
  ARRAY((byte) 0, ArrayH.class, ArrayH::new),
  BLOB((byte) 1, BlobH.class, BlobH::new),
  BOOL((byte) 2, BoolH.class, BoolH::new),
  ABSTRACT_FUNCTION((byte) 3, FunctionH.class, SpecKindH::throwException),
  INT((byte) 4, IntH.class, IntH::new),
  IF_FUNCTION((byte) 5, IfFunctionH.class, IfFunctionH::new),
  NOTHING((byte) 6, ValueH.class, SpecKindH::throwException),
  TUPLE((byte) 7, TupleH.class, TupleH::new),
  STRING((byte) 8, StringH.class, StringH::new),

  CALL((byte) 9, CallH.class, CallH::new),
  // 10 unused
  ORDER((byte) 11, OrderH.class, OrderH::new),
  SELECT((byte) 12, SelectH.class, SelectH::new),
  REF((byte) 14, RefH.class, RefH::new),
  CONSTRUCT((byte) 15, ConstructH.class, ConstructH::new),

  VARIABLE((byte) 17, ValueH.class, SpecKindH::throwException),
  ANY((byte) 18, ValueH.class, SpecKindH::throwException),
  NATIVE_FUNCTION((byte) 19, NativeFunctionH.class, NativeFunctionH::new),
  // 20 IS UNUSED
  MAP_FUNCTION((byte) 21, MapFunctionH.class, MapFunctionH::new),
  DEFINED_FUNCTION((byte) 22, DefinedFunctionH.class, DefinedFunctionH::new);
  private static ObjectH throwException(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    throw new UnsupportedOperationException();
  }

  private static final ImmutableMap<Byte, SpecKindH> markerToObjKindMap =
      ImmutableMap.<Byte, SpecKindH>builder()
          .put((byte) 0, ARRAY)
          .put((byte) 1, BLOB)
          .put((byte) 2, BOOL)
          .put((byte) 3, ABSTRACT_FUNCTION)
          .put((byte) 4, INT)
          .put((byte) 5, IF_FUNCTION)
          .put((byte) 6, NOTHING)
          .put((byte) 7, TUPLE)
          .put((byte) 8, STRING)

          .put((byte) 9, CALL)
          // 10 unused
          .put((byte) 11, ORDER)
          .put((byte) 12, SELECT)
          .put((byte) 14, REF)
          .put((byte) 15, CONSTRUCT)
          .put((byte) 17, VARIABLE)
          .put((byte) 18, ANY)
          .put((byte) 19, NATIVE_FUNCTION)
          // 20 unused
          .put((byte) 21, MAP_FUNCTION)
          .put((byte) 22, DEFINED_FUNCTION)
          .build();

  private final byte marker;
  private final Class<? extends ObjectH> typeJ;
  private final BiFunction<MerkleRoot, ObjectHDb, ObjectH> instantiator;

  SpecKindH(byte marker, Class<? extends ObjectH> typeJ,
      BiFunction<MerkleRoot, ObjectHDb, ObjectH> instantiator) {
    this.marker = marker;
    this.typeJ = typeJ;
    this.instantiator = instantiator;
  }

  public static SpecKindH fromMarker(byte marker) {
    return markerToObjKindMap.get(marker);
  }

  public byte marker() {
    return marker;
  }

  public Class<? extends ObjectH> typeJ() {
    return typeJ;
  }

  public ObjectH newInstanceJ(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return instantiator.apply(merkleRoot, objectHDb);
  }
}

package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;

import org.smoothbuild.common.collect.Maybe;

public enum KindId {
  BLOB,
  BOOL,
  INT,
  STRING,
  ARRAY,
  TUPLE,
  LAMBDA,
  INVOKE,
  CREATE_ARRAY,
  CREATE_TUPLE,
  TUPLE_GET,
  CALL,
  ARRAY_GET,
  IF,
  REF,
  MAP,
  CREATE_VARIANT,
  VARIANT,
  SWITCH,
  FOLD,
  ;

  @SuppressWarnings("EnumOrdinal")
  public byte byteMarker() {
    return (byte) ordinal();
  }

  private static final KindId[] VALUES = values();

  public static Maybe<KindId> fromOrdinal(int ordinal) {
    if (0 <= ordinal && ordinal < VALUES.length) {
      return some(VALUES[ordinal]);
    }
    return none();
  }
}

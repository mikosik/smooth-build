package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.oper.PickCB;
import org.smoothbuild.bytecode.type.value.TypeB;

public class DecodePickWrongEvalTypeExc extends DecodeExprExc {
  public DecodePickWrongEvalTypeExc(Hash hash, PickCB cat, TypeB actualElemType) {
    super(buildMessage(hash, cat, actualElemType));
  }

  private static String buildMessage(Hash hash, PickCB cat, TypeB actualElemType) {
    return ("Cannot decode %s object at %s. Its pickable is array with elem type %s while this "
        + "expression defines its evaluation type as %s.")
        .formatted(cat.q(), hash, actualElemType.q(), cat.evalT().q());
  }
}

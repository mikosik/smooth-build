package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.oper.PickCB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class DecodePickWrongEvaluationTypeExc extends DecodeExprExc {
  public DecodePickWrongEvaluationTypeExc(Hash hash, PickCB category, TypeB actualElemType) {
    super(buildMessage(hash, category, actualElemType));
  }

  private static String buildMessage(Hash hash, PickCB category, TypeB actualElemType) {
    return ("Cannot decode %s object at %s. Its pickable is array with elem type %s while this "
        + "expression defines its evaluation type as %s.")
        .formatted(category.q(), hash, actualElemType.q(), category.evaluationT().q());
  }
}

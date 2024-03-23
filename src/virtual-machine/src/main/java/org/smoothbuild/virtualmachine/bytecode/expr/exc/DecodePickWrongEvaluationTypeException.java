package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BPickCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class DecodePickWrongEvaluationTypeException extends DecodeExprException {
  public DecodePickWrongEvaluationTypeException(
      Hash hash, BPickCategory category, BType actualElemType) {
    super(buildMessage(hash, category, actualElemType));
  }

  private static String buildMessage(Hash hash, BPickCategory category, BType actualElemType) {
    return ("Cannot decode %s object at %s. Its pickable is array with elem type %s while this "
            + "expression defines its evaluation type as %s.")
        .formatted(
            category.q(), hash, actualElemType.q(), category.evaluationType().q());
  }
}

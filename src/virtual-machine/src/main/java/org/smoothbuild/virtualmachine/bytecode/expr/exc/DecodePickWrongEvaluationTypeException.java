package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BPickKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class DecodePickWrongEvaluationTypeException extends DecodeExprException {
  public DecodePickWrongEvaluationTypeException(Hash hash, BPickKind kind, BType actualElemType) {
    super(buildMessage(hash, kind, actualElemType));
  }

  private static String buildMessage(Hash hash, BPickKind kind, BType actualElemType) {
    return ("Cannot decode %s object at %s. Its pickable is array with elem type %s while this "
            + "expression defines its evaluation type as %s.")
        .formatted(kind.q(), hash, actualElemType.q(), kind.evaluationType().q());
  }
}

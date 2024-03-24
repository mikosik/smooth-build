package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BSelectKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class DecodeSelectWrongEvaluationTypeException extends DecodeExprException {
  public DecodeSelectWrongEvaluationTypeException(Hash hash, BSelectKind kind, BType actual) {
    super(buildMessage(hash, kind, actual));
  }

  private static String buildMessage(Hash hash, BSelectKind kind, BType actual) {
    return ("Cannot decode %s object at %s. Its index points to item with %s type while this "
            + "expression defines its evaluation type as %s.")
        .formatted(kind.q(), hash, actual.q(), kind.evaluationType().q());
  }
}

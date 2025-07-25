package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSelectKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class SelectHasWrongEvaluationTypeException extends DecodeExprException {
  public SelectHasWrongEvaluationTypeException(Hash hash, BSelectKind kind, BType actual) {
    super(buildMessage(hash, kind, actual));
  }

  private static String buildMessage(Hash hash, BSelectKind kind, BType actual) {
    return ("Cannot decode %s expression at %s. Its index points to item with %s type while this "
            + "expression defines its evaluation type as %s.")
        .formatted(kind.q(), hash, actual.q(), kind.evaluationType().q());
  }
}

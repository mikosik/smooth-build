package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BSelectCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class DecodeSelectWrongEvaluationTypeException extends DecodeExprException {
  public DecodeSelectWrongEvaluationTypeException(
      Hash hash, BSelectCategory category, BType actual) {
    super(buildMessage(hash, category, actual));
  }

  private static String buildMessage(Hash hash, BSelectCategory category, BType actual) {
    return ("Cannot decode %s object at %s. Its index points to item with %s type while this "
            + "expression defines its evaluation type as %s.")
        .formatted(category.q(), hash, actual.q(), category.evaluationType().q());
  }
}

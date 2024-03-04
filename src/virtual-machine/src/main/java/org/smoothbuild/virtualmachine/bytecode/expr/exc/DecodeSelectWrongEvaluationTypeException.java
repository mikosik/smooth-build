package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.oper.SelectCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class DecodeSelectWrongEvaluationTypeException extends DecodeExprException {
  public DecodeSelectWrongEvaluationTypeException(Hash hash, SelectCB category, TypeB actual) {
    super(buildMessage(hash, category, actual));
  }

  private static String buildMessage(Hash hash, SelectCB category, TypeB actual) {
    return ("Cannot decode %s object at %s. Its index points to item with %s type while this "
            + "expression defines its evaluation type as %s.")
        .formatted(category.q(), hash, actual.q(), category.evaluationType().q());
  }
}

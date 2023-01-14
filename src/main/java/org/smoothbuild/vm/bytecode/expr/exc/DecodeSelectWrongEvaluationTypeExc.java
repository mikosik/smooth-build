package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.oper.SelectCB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class DecodeSelectWrongEvaluationTypeExc extends DecodeExprExc {
  public DecodeSelectWrongEvaluationTypeExc(Hash hash, SelectCB category, TypeB actual) {
    super(buildMessage(hash, category, actual));
  }

  private static String buildMessage(Hash hash, SelectCB category, TypeB actual) {
    return ("Cannot decode %s object at %s. Its index points to item with %s type while this "
        + "expression defines its evaluation type as %s.")
        .formatted(category.q(), hash, actual.q(), category.evaluationT().q());
  }
}

package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.oper.SelectCB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class DecodeSelectWrongEvalTypeExc extends DecodeExprExc {
  public DecodeSelectWrongEvalTypeExc(Hash hash, SelectCB cat, TypeB actual) {
    super(buildMessage(hash, cat, actual));
  }

  private static String buildMessage(Hash hash, SelectCB cat, TypeB actual) {
    return ("Cannot decode %s object at %s. Its index points to item with %s type while this "
        + "expression defines its evaluation type as %s.")
        .formatted(cat.q(), hash, actual.q(), cat.evalT().q());
  }
}

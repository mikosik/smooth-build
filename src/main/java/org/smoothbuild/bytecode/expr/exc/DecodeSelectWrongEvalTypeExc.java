package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.bytecode.type.oper.SelectCB;

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

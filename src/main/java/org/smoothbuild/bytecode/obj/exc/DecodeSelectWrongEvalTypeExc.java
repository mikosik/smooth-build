package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.expr.SelectCB;
import org.smoothbuild.db.hashed.Hash;

public class DecodeSelectWrongEvalTypeExc extends DecodeObjExc {
  public DecodeSelectWrongEvalTypeExc(Hash hash, SelectCB cat, TypeB actual) {
    super(buildMessage(hash, cat, actual));
  }

  private static String buildMessage(Hash hash, SelectCB cat, TypeB actual) {
    return ("Cannot decode %s object at %s. Its index points to item with %s type while this "
        + "expression defines its evaluation type as %s.")
        .formatted(cat.q(), hash, actual.q(), cat.evalT().q());
  }
}

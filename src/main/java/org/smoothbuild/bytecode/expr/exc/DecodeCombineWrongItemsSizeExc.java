package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.oper.CombineCB;

public class DecodeCombineWrongItemsSizeExc extends DecodeExprExc {
  public DecodeCombineWrongItemsSizeExc(Hash hash, CombineCB cat, int actual) {
    super(buildMessage(hash, cat, actual));
  }

  private static String buildMessage(Hash hash, CombineCB cat, int actual) {
    return ("Cannot decode %s object at %s. Evaluation type items size (%s)"
        + " is not equal to actual items size (%s).")
        .formatted(
            cat.q(),
            hash,
            cat.evalT().items().size(),
            actual);
  }
}

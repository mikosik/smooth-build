package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.oper.CombineCB;

public class DecodeCombineWrongElementsSizeExc extends DecodeExprExc {
  public DecodeCombineWrongElementsSizeExc(Hash hash, CombineCB cat, int actual) {
    super(buildMessage(hash, cat, actual));
  }

  private static String buildMessage(Hash hash, CombineCB cat, int actual) {
    return ("Cannot decode %s object at %s. Evaluation type elements size (%s)"
        + " is not equal to actual elements size (%s).")
        .formatted(
            cat.q(),
            hash,
            cat.evaluationT().elements().size(),
            actual);
  }
}

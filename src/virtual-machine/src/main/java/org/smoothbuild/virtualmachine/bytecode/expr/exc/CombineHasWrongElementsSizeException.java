package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCombineKind;

public class CombineHasWrongElementsSizeException extends DecodeExprException {
  public CombineHasWrongElementsSizeException(Hash hash, BCombineKind kind, int actual) {
    super(buildMessage(hash, kind, actual));
  }

  private static String buildMessage(Hash hash, BCombineKind kind, int actual) {
    return ("Cannot decode %s expression at %s. Evaluation type elements size (%s)"
            + " is not equal to actual elements size (%s).")
        .formatted(kind.q(), hash, kind.evaluationType().elements().size(), actual);
  }
}
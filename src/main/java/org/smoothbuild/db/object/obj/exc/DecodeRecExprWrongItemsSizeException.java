package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.expr.RecExprSpec;

public class DecodeRecExprWrongItemsSizeException extends DecodeObjException {
  public DecodeRecExprWrongItemsSizeException(Hash hash, RecExprSpec spec, int actual) {
    super(buildMessage(hash, spec, actual));
  }

  private static String buildMessage(Hash hash, RecExprSpec spec, int actual) {
    return ("Cannot decode %s object at %s. Evaluation spec items size (%s)"
        + " is not equal to actual items size (%s).")
        .formatted(
            spec.q(),
            hash,
            spec.evaluationSpec().items().size(),
            actual);
  }
}

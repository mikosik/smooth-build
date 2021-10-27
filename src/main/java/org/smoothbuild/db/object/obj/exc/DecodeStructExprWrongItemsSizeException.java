package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.expr.StructExprSpec;

public class DecodeStructExprWrongItemsSizeException extends DecodeObjException {
  public DecodeStructExprWrongItemsSizeException(Hash hash, StructExprSpec spec, int actual) {
    super(buildMessage(hash, spec, actual));
  }

  private static String buildMessage(Hash hash, StructExprSpec spec, int actual) {
    return ("Cannot decode %s object at %s. Evaluation spec items size (%s)"
        + " is not equal to actual items size (%s).")
        .formatted(
            spec.q(),
            hash,
            spec.evaluationSpec().fields().size(),
            actual);
  }
}

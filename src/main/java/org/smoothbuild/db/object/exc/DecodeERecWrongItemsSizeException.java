package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.expr.ERecSpec;

public class DecodeERecWrongItemsSizeException extends DecodeObjException {
  public DecodeERecWrongItemsSizeException(Hash hash, ERecSpec spec, int actual) {
    super(buildMessage(hash, spec, actual));
  }

  private static String buildMessage(Hash hash, ERecSpec spec, int actual) {
    return ("Cannot decode %s object at %s. Evaluation spec items size (%s)"
        + " is not equal to actual items size (%s).")
        .formatted(
            spec.name(),
            hash,
            spec.evaluationSpec().items().size(),
            actual);
  }
}

package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.expr.CombineTypeH;

public class DecodeCombineWrongItemsSizeException extends DecodeObjException {
  public DecodeCombineWrongItemsSizeException(Hash hash, CombineTypeH type, int actual) {
    super(buildMessage(hash, type, actual));
  }

  private static String buildMessage(Hash hash, CombineTypeH type, int actual) {
    return ("Cannot decode %s object at %s. Evaluation type items size (%s)"
        + " is not equal to actual items size (%s).")
        .formatted(
            type.q(),
            hash,
            type.evalType().items().size(),
            actual);
  }
}

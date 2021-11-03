package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.expr.StructExprOType;

public class DecodeStructExprWrongItemsSizeException extends DecodeObjException {
  public DecodeStructExprWrongItemsSizeException(Hash hash, StructExprOType type, int actual) {
    super(buildMessage(hash, type, actual));
  }

  private static String buildMessage(Hash hash, StructExprOType type, int actual) {
    return ("Cannot decode %s object at %s. Evaluation type items size (%s)"
        + " is not equal to actual items size (%s).")
        .formatted(
            type.q(),
            hash,
            type.evaluationType().fields().size(),
            actual);
  }
}

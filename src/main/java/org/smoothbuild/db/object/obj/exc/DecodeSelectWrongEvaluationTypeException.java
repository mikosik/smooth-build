package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.expr.SelectOType;

public class DecodeSelectWrongEvaluationTypeException extends DecodeObjException {
  public DecodeSelectWrongEvaluationTypeException(Hash hash, SelectOType type, TypeV actual) {
    super(buildMessage(hash, type, actual));
  }

  private static String buildMessage(Hash hash, SelectOType type, TypeV actual) {
    return ("Cannot decode %s object at %s. Its index points to item with %s type while this "
        + "expression defines its evaluation type as %s.")
        .formatted(type.q(), hash, actual.q(), type.evaluationType().q());
  }
}

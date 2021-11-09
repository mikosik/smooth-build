package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.expr.SelectTypeH;

public class DecodeSelectWrongEvaluationTypeException extends DecodeObjException {
  public DecodeSelectWrongEvaluationTypeException(Hash hash, SelectTypeH type, TypeHV actual) {
    super(buildMessage(hash, type, actual));
  }

  private static String buildMessage(Hash hash, SelectTypeH type, TypeHV actual) {
    return ("Cannot decode %s object at %s. Its index points to item with %s type while this "
        + "expression defines its evaluation type as %s.")
        .formatted(type.q(), hash, actual.q(), type.evaluationType().q());
  }
}

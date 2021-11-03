package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.ObjType;
import org.smoothbuild.db.object.type.base.ValType;

public class DecodeExprWrongEvaluationTypeOfComponentException extends DecodeObjException {
  public DecodeExprWrongEvaluationTypeOfComponentException(Hash hash, ObjType type,
      String component, Class<?> expected, ValType actual) {
    super(buildMessage(hash, type, component, expected.getCanonicalName(), actual));
  }

  public DecodeExprWrongEvaluationTypeOfComponentException(Hash hash, ObjType type,
      String component, ValType expected, ValType actual) {
    super(buildMessage(hash, type, component, expected.q(), actual));
  }

  private static String buildMessage(Hash hash, ObjType type, String component,
      String expected, ValType actual) {
    return ("Cannot decode %s object at %s. Its `%s` component evaluation type is %s while "
        + "expected %s.")
        .formatted(type.q(), hash, component, actual.q(), expected);
  }
}

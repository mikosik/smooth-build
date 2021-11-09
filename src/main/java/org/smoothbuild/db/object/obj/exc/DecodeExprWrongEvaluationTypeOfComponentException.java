package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.base.TypeHV;

public class DecodeExprWrongEvaluationTypeOfComponentException extends DecodeObjException {
  public DecodeExprWrongEvaluationTypeOfComponentException(Hash hash, TypeH type,
      String component, Class<?> expected, TypeHV actual) {
    super(buildMessage(hash, type, component, expected.getCanonicalName(), actual));
  }

  public DecodeExprWrongEvaluationTypeOfComponentException(Hash hash, TypeH type,
      String component, TypeHV expected, TypeHV actual) {
    super(buildMessage(hash, type, component, expected.q(), actual));
  }

  private static String buildMessage(Hash hash, TypeH type, String component,
      String expected, TypeHV actual) {
    return ("Cannot decode %s object at %s. Its `%s` component evaluation type is %s while "
        + "expected %s.")
        .formatted(type.q(), hash, component, actual.q(), expected);
  }
}

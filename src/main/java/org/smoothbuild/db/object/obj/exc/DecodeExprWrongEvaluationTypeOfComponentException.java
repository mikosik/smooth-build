package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeO;
import org.smoothbuild.db.object.type.base.TypeV;

public class DecodeExprWrongEvaluationTypeOfComponentException extends DecodeObjException {
  public DecodeExprWrongEvaluationTypeOfComponentException(Hash hash, TypeO type,
      String component, Class<?> expected, TypeV actual) {
    super(buildMessage(hash, type, component, expected.getCanonicalName(), actual));
  }

  public DecodeExprWrongEvaluationTypeOfComponentException(Hash hash, TypeO type,
      String component, TypeV expected, TypeV actual) {
    super(buildMessage(hash, type, component, expected.q(), actual));
  }

  private static String buildMessage(Hash hash, TypeO type, String component,
      String expected, TypeV actual) {
    return ("Cannot decode %s object at %s. Its `%s` component evaluation type is %s while "
        + "expected %s.")
        .formatted(type.q(), hash, component, actual.q(), expected);
  }
}

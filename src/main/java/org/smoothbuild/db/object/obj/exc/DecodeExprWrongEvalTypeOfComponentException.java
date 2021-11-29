package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.base.TypeH;

public class DecodeExprWrongEvalTypeOfComponentException extends DecodeObjException {
  public DecodeExprWrongEvalTypeOfComponentException(Hash hash, SpecH type,
      String component, Class<?> expected, TypeH actual) {
    super(buildMessage(hash, type, component, expected.getCanonicalName(), actual));
  }

  public DecodeExprWrongEvalTypeOfComponentException(Hash hash, SpecH type,
      String component, TypeH expected, TypeH actual) {
    super(buildMessage(hash, type, component, expected.q(), actual));
  }

  private static String buildMessage(Hash hash, SpecH type, String component,
      String expected, TypeH actual) {
    return ("Cannot decode %s object at %s. Its `%s` component evaluation type is %s while "
        + "expected %s.")
        .formatted(type.q(), hash, component, actual.q(), expected);
  }
}

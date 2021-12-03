package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.TypeH;

public class DecodeExprWrongEvalTypeOfCompExc extends DecodeObjExc {
  public DecodeExprWrongEvalTypeOfCompExc(Hash hash, CatH cat,
      String component, Class<?> expected, TypeH actual) {
    super(buildMessage(hash, cat, component, expected.getCanonicalName(), actual));
  }

  public DecodeExprWrongEvalTypeOfCompExc(Hash hash, CatH cat,
      String component, TypeH expected, TypeH actual) {
    super(buildMessage(hash, cat, component, expected.q(), actual));
  }

  private static String buildMessage(Hash hash, CatH cat, String component,
      String expected, TypeH actual) {
    return ("Cannot decode %s object at %s. Its `%s` component evaluation type is %s while "
        + "expected %s.")
        .formatted(cat.q(), hash, component, actual.q(), expected);
  }
}

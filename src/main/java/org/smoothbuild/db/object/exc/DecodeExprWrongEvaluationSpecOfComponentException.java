package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;

public class DecodeExprWrongEvaluationSpecOfComponentException extends DecodeObjException {
  public DecodeExprWrongEvaluationSpecOfComponentException(Hash hash, Spec spec, String component,
      Class<?> expected, ValSpec actual) {
    super(buildMessage(hash, spec, component, expected.getCanonicalName(), actual));
  }

  public DecodeExprWrongEvaluationSpecOfComponentException(Hash hash, Spec spec, String component,
      ValSpec expected, ValSpec actual) {
    super(buildMessage(hash, spec, component, expected.q(), actual));
  }

  private static String buildMessage(Hash hash, Spec spec, String component,
      String expected, ValSpec actual) {
    return ("Cannot decode %s object at %s. Its `%s` component evaluation spec is %s while "
        + "expected %s.")
        .formatted(spec.q(), hash, component, actual.q(), expected);
  }
}

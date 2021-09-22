package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;

public class DecodeExprWrongEvaluationSpecOfComponentException extends DecodeObjException {
  public DecodeExprWrongEvaluationSpecOfComponentException(Hash hash, Spec spec, String component,
      ValSpec actual, Class<?> expected) {
    super(buildMessage(hash, spec, component, actual, expected.getCanonicalName()));
  }

  public DecodeExprWrongEvaluationSpecOfComponentException(Hash hash, Spec spec, String component,
      ValSpec actual, ValSpec expected) {
    super(buildMessage(hash, spec, component, actual, expected.name()));
  }

  private static String buildMessage(Hash hash, Spec spec, String component,
      ValSpec actual, String expected) {
    return ("Cannot decode %s object at %s. Its `%s` component evaluation spec is %s while "
        + "expected %s.")
        .formatted(spec.name(), hash, component, actual.name(), expected);
  }
}

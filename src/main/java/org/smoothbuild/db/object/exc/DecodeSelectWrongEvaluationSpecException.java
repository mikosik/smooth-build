package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.SelectSpec;

public class DecodeSelectWrongEvaluationSpecException extends DecodeObjException {
  public DecodeSelectWrongEvaluationSpecException(Hash hash, SelectSpec spec, ValSpec actual) {
    super(buildMessage(hash, spec, actual));
  }

  private static String buildMessage(Hash hash, SelectSpec spec, ValSpec actual) {
    return ("Cannot decode %s object at %s. Its index points to item with %s spec while this "
        + "expression defines its evaluation spec as %s.")
        .formatted(spec.q(), hash, actual.q(), spec.evaluationSpec().q());
  }
}

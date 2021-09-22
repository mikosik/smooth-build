package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.FieldReadSpec;

public class DecodeSelectWrongEvaluationSpec extends DecodeObjException {
  public DecodeSelectWrongEvaluationSpec(Hash hash, FieldReadSpec spec, ValSpec actual) {
    super(buildMessage(hash, spec, actual));
  }

  private static String buildMessage(Hash hash, FieldReadSpec spec, ValSpec actual) {
    return ("Cannot decode %s object at %s. Its index points to item with %s spec while this "
        + "expression defines its evaluation spec as %s.")
        .formatted(spec.name(), hash, actual.name(), spec.evaluationSpec().name());
  }
}

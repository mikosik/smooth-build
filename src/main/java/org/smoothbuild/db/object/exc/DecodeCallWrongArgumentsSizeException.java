package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.expr.CallSpec;

public class DecodeCallWrongArgumentsSizeException extends DecodeObjException {
  public DecodeCallWrongArgumentsSizeException(Hash hash, CallSpec spec, int expected, int actual) {
    super(buildMessage(hash, spec, expected, actual));
  }

  private static String buildMessage(Hash hash, CallSpec spec, int expected, int actual) {
    return ("Cannot decode %s object at %s. Function evaluation spec parameters size (%s)"
        + " is not equal to arguments size (%s).")
        .formatted(
            spec.name(),
            hash,
            expected,
            actual);
  }
}

package org.smoothbuild.db.object.spec.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.SpecKind;

public class DecodeVariableIllegalNameException extends DecodeSpecException {
  public DecodeVariableIllegalNameException(Hash hash, String name) {
    super(hash, "It is " + SpecKind.VARIABLE + " with illegal name `" + name + "`.");
  }
}

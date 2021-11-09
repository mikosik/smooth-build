package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeKindH;

public class DecodeVariableIllegalNameException extends DecodeTypeException {
  public DecodeVariableIllegalNameException(Hash hash, String name) {
    super(hash, "It is " + TypeKindH.VARIABLE + " with illegal name `" + name + "`.");
  }
}

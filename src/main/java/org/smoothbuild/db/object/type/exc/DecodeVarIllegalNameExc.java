package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.SpecKindH;

public class DecodeVarIllegalNameExc extends DecodeTypeExc {
  public DecodeVarIllegalNameExc(Hash hash, String name) {
    super(hash, "It is " + SpecKindH.VARIABLE + " with illegal name `" + name + "`.");
  }
}

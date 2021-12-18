package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatKindB;

public class DecodeVarIllegalNameExc extends DecodeCatExc {
  public DecodeVarIllegalNameExc(Hash hash, String name) {
    super(hash, "It is " + CatKindB.VARIABLE + " with illegal name `" + name + "`.");
  }
}

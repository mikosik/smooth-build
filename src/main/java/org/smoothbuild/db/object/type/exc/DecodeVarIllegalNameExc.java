package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatKindH;

public class DecodeVarIllegalNameExc extends DecodeCatExc {
  public DecodeVarIllegalNameExc(Hash hash, String name) {
    super(hash, "It is " + CatKindH.VARIABLE + " with illegal name `" + name + "`.");
  }
}

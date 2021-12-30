package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.db.Hash;

public class DecodeVarIllegalNameExc extends DecodeCatExc {
  public DecodeVarIllegalNameExc(Hash hash, String name) {
    super(hash, "It is " + CatKindB.VARIABLE + " with illegal name `" + name + "`.");
  }
}

package org.smoothbuild.bytecode.type.exc;

import static org.smoothbuild.bytecode.type.base.CatKindB.OPEN_VARIABLE;

import org.smoothbuild.db.Hash;

public class DecodeVarIllegalNameExc extends DecodeCatExc {
  public DecodeVarIllegalNameExc(Hash hash, String name) {
    super(hash, "It is " + OPEN_VARIABLE + " with illegal name `" + name + "`.");
  }
}

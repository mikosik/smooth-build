package org.smoothbuild.bytecode.type.exc;

import static org.smoothbuild.bytecode.type.base.CatKindB.VAR;

import org.smoothbuild.db.Hash;

public class DecodeVarIllegalNameExc extends DecodeCatExc {
  public DecodeVarIllegalNameExc(Hash hash, String name) {
    super(hash, "It is " + VAR + " with illegal name `" + name + "`.");
  }
}

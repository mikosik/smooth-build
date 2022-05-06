package org.smoothbuild.bytecode.type.exc;

import static org.smoothbuild.bytecode.type.CatKindB.VAR;

import org.smoothbuild.db.Hash;

public class DecodeCatIllegalVarNameExc extends DecodeCatExc {
  public DecodeCatIllegalVarNameExc(Hash hash, String name) {
    super(hash, "It is " + VAR + " with illegal name `" + name + "`.");
  }
}

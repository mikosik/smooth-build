package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.type.val.VarTB;
import org.smoothbuild.db.Hash;

public class DecodeCatTypeParamDuplicatedVarExc extends DecodeCatExc {
  public DecodeCatTypeParamDuplicatedVarExc(Hash hash, VarTB var) {
    super(hash, "Its type parameters contains duplicated variable " + var.q() + ".");
  }
}

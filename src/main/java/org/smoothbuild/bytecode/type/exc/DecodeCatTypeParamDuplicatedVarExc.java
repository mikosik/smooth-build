package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.db.Hash;

public class DecodeCatTypeParamDuplicatedVarExc extends DecodeCatExc {
  public DecodeCatTypeParamDuplicatedVarExc(Hash hash, VarB var) {
    super(hash, "Its type parameters contains duplicated variable " + var.q() + ".");
  }
}

package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.db.Hash;

public class DecodeCatTParamDuplicatedVarExc extends DecodeCatExc {
  public DecodeCatTParamDuplicatedVarExc(Hash hash, VarB var) {
    super(hash, "Its type parameters contains duplicated variable " + var.q() + ".");
  }
}

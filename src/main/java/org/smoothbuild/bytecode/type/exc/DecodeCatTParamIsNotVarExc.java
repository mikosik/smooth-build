package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.db.Hash;

public class DecodeCatTParamIsNotVarExc extends DecodeCatExc {
  public DecodeCatTParamIsNotVarExc(Hash hash, TypeB type) {
    super(hash, "Its type parameters contains type which is not variable but " + type.q() + ".");
  }
}

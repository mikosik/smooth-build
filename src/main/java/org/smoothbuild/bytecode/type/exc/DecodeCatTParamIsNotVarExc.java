package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;

public class DecodeCatTParamIsNotVarExc extends DecodeCatExc {
  public DecodeCatTParamIsNotVarExc(Hash hash, TypeB type) {
    super(hash, "Its type parameters contains type which is not variable but " + type.q() + ".");
  }
}

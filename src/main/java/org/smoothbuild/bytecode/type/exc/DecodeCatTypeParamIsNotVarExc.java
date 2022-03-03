package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;

public class DecodeCatTypeParamIsNotVarExc extends DecodeCatExc {
  public DecodeCatTypeParamIsNotVarExc(Hash hash, TypeB type) {
    super(hash, "Its type parameters contains type which is not variable but " + type.q() + ".");
  }
}

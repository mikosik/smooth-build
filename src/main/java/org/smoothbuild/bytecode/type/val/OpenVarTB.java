package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.OPEN_VARIABLE;

import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.OpenVarT;

public class OpenVarTB extends VarTB implements OpenVarT {
  public OpenVarTB(Hash hash, String name) {
    super(hash, name, OPEN_VARIABLE, true, false);
  }
}

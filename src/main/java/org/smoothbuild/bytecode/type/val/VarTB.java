package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.VarT;

public class VarTB extends TypeB implements VarT {
  public VarTB(Hash hash, String name, CatKindB kind, boolean hasOpenVars, boolean hasClosedVars) {
    super(name, hash, kind, hasOpenVars, hasClosedVars);
  }
}

package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.VarT;

import com.google.common.collect.ImmutableSet;

public class VarTB extends TypeB implements VarT {
  public VarTB(Hash hash, String name, CatKindB kind, ImmutableSet<OpenVarTB> openVars,
      boolean hasClosedVars) {
    super(hash, name, kind, openVars, hasClosedVars);
  }
}

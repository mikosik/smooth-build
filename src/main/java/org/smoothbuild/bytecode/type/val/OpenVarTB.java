package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.OPEN_VARIABLE;

import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.type.api.OpenVarT;

import com.google.common.collect.ImmutableSet;

public class OpenVarTB extends VarTB implements OpenVarT {
  private final ImmutableSet<OpenVarTB> openVars;

  public OpenVarTB(Hash hash, String name) {
    super(hash, name, OPEN_VARIABLE, null, false);
    this.openVars = ImmutableSet.of(this);
  }

  @Override
  public ImmutableSet<OpenVarTB> openVars() {
    return openVars;
  }
}

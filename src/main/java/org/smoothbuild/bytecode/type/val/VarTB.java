package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.VAR;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.type.api.VarT;

public class VarTB extends TypeB implements VarT {
  private final VarSetB vars;

  public VarTB(Hash hash, String name) {
    super(hash, name, VAR, null);
    this.vars = new VarSetB(set(this));
  }

  @Override
  public VarSetB vars() {
    return vars;
  }
}

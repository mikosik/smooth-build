package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.VAR;
import static org.smoothbuild.bytecode.type.val.VarSetB.varSetB;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.db.Hash;

public class VarB extends TypeB {
  private final VarSetB vars;

  public VarB(Hash hash, String name) {
    super(hash, name, VAR, varSetB(), null);
    this.vars = new VarSetB(set(this));
  }

  @Override
  public VarSetB vars() {
    return vars;
  }
}

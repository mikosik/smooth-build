package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.VAR;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.db.Hash;

/**
 * Type variable.
 */
public class VarB extends TypeB {
  private final VarSetB vars;

  public VarB(Hash hash, String name) {
    super(hash, name, VAR, null);
    this.vars = new VarSetB(set(this));
  }

  @Override
  public VarSetB vars() {
    return vars;
  }
}

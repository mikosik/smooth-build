package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.Var;

import com.google.common.collect.ImmutableSet;

public class VarTB extends TypeB implements Var {
  private final ImmutableSet<VarTB> vars;

  public VarTB(Hash hash, String name) {
    super(name, hash, CatKindB.VARIABLE);
    this.vars = set(this);
  }

  @Override
  public ImmutableSet<VarTB> vars() {
    return vars;
  }
}

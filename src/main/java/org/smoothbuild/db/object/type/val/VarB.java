package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindB.VARIABLE;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.lang.base.type.api.Var;

import com.google.common.collect.ImmutableSet;

public class VarB extends TypeB implements Var {
  private final ImmutableSet<VarB> vars;

  public VarB(Hash hash, String name) {
    super(name, hash, VARIABLE);
    this.vars = set(this);
  }

  @Override
  public ImmutableSet<VarB> vars() {
    return vars;
  }
}

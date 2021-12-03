package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.VARIABLE;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.Var;

import com.google.common.collect.ImmutableSet;

public class VarH extends TypeH implements Var {
  private final ImmutableSet<VarH> vars;

  public VarH(Hash hash, String name) {
    super(name, hash, VARIABLE);
    this.vars = set(this);
  }

  @Override
  public ImmutableSet<VarH> vars() {
    return vars;
  }
}

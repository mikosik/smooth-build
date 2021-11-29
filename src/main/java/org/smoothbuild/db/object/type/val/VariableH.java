package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.VARIABLE;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public class VariableH extends TypeH implements Variable {
  private final ImmutableSet<VariableH> variables;

  public VariableH(Hash hash, String name) {
    super(name, hash, VARIABLE);
    this.variables = set(this);
  }

  @Override
  public ImmutableSet<VariableH> variables() {
    return variables;
  }
}

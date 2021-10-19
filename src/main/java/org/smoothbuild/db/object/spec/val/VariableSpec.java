package org.smoothbuild.db.object.spec.val;

import static org.smoothbuild.db.object.spec.base.SpecKind.VARIABLE;
import static org.smoothbuild.util.Sets.set;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public class VariableSpec extends ValSpec implements Variable {
  private final String name;
  private final ImmutableSet<Variable> variables;

  public VariableSpec(Hash hash, String name) {
    super(name, hash, VARIABLE);
    this.name = name;
    this.variables = set(this);
  }

  @Override
  public ImmutableSet<Variable> variables() {
    return variables;
  }

  @Override
  public Obj newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    throw new UnsupportedOperationException("Cannot create object for " + VARIABLE + " spec.");
  }
}

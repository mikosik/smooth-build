package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.VARIABLE;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public class VariableH extends TypeHV implements Variable {
  private final String name;
  private final ImmutableSet<Variable> variables;

  public VariableH(Hash hash, String name) {
    super(name, hash, VARIABLE);
    this.name = name;
    this.variables = set(this);
  }

  @Override
  public ImmutableSet<Variable> variables() {
    return variables;
  }

  @Override
  public ObjectH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    throw new UnsupportedOperationException("Cannot create object for " + VARIABLE + " type.");
  }
}

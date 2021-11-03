package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.ObjKind.VARIABLE;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public class VariableOType extends ValType implements Variable {
  private final String name;
  private final ImmutableSet<Variable> variables;

  public VariableOType(Hash hash, String name) {
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
    throw new UnsupportedOperationException("Cannot create object for " + VARIABLE + " type.");
  }
}

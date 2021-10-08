package org.smoothbuild.db.object.spec.val;

import static org.smoothbuild.db.object.spec.base.SpecKind.VARIABLE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.lang.base.type.api.Variable;

public class VariableSpec extends ValSpec implements Variable {
  private final String name;

  public VariableSpec(Hash hash, String name) {
    super(hash, VARIABLE);
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Obj newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    throw new UnsupportedOperationException("Cannot create object for " + kind() + " spec.");
  }
}

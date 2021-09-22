package org.smoothbuild.db.object.obj.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.spec.base.ExprSpec;
import org.smoothbuild.db.object.spec.base.ValSpec;

public abstract class Expr extends Obj {
  public Expr(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof ExprSpec);
  }

  @Override
  public ExprSpec spec() {
    return (ExprSpec) super.spec();
  }

  public ValSpec evaluationSpec() {
    return spec().evaluationSpec();
  }
}

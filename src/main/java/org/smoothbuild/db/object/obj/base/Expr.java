package org.smoothbuild.db.object.obj.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.type.base.ExprType;
import org.smoothbuild.db.object.type.base.ValType;

public abstract class Expr extends Obj {
  public Expr(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.type() instanceof ExprType);
  }

  @Override
  public ExprType type() {
    return (ExprType) super.type();
  }

  public ValType evaluationType() {
    return type().evaluationType();
  }
}

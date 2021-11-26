package org.smoothbuild.db.object.obj.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.base.TypeHV;

public abstract class ExprH extends ObjectH implements EvaluableH {
  public ExprH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.type() instanceof TypeHE);
  }

  @Override
  public TypeHE type() {
    return (TypeHE) super.type();
  }

  @Override
  public TypeHV evaluationType() {
    return type().evaluationType();
  }
}

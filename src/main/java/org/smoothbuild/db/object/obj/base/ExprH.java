package org.smoothbuild.db.object.obj.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.type.base.ExprSpecH;
import org.smoothbuild.db.object.type.base.TypeH;

public abstract class ExprH extends ObjectH implements EvaluableH {
  public ExprH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.spec() instanceof ExprSpecH);
  }

  @Override
  public ExprSpecH spec() {
    return (ExprSpecH) super.spec();
  }

  @Override
  public TypeH type() {
    return spec().evaluationType();
  }
}

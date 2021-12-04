package org.smoothbuild.db.object.obj.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.type.base.ExprCatH;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * Expression.
 * This class is thread-safe.
 */
public abstract class ExprH extends ObjH {
  public ExprH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.cat() instanceof ExprCatH);
  }

  @Override
  public ExprCatH cat() {
    return (ExprCatH) super.cat();
  }

  @Override
  public TypeH type() {
    return cat().evalT();
  }

  @Override
  public String objToString() {
    return cat().name() + "(???)";
  }
}

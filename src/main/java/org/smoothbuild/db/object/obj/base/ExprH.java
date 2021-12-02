package org.smoothbuild.db.object.obj.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.type.base.ExprSpecH;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * Expression.
 * This class is thread-safe.
 */
public abstract class ExprH extends ObjH {
  public ExprH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.spec() instanceof ExprSpecH);
  }

  @Override
  public ExprSpecH spec() {
    return (ExprSpecH) super.spec();
  }

  @Override
  public TypeH type() {
    return spec().evalType();
  }

  @Override
  public String objToString() {
    return spec().name() + "(???)";
  }
}

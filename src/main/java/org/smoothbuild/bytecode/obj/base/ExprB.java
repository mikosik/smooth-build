package org.smoothbuild.bytecode.obj.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.type.expr.ExprCatB;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * Expression.
 * This class is thread-safe.
 */
public abstract class ExprB extends ObjB {
  public ExprB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.cat() instanceof ExprCatB);
  }

  @Override
  public ExprCatB cat() {
    return (ExprCatB) super.cat();
  }

  @Override
  public TypeB type() {
    return cat().evalT();
  }

  @Override
  public String objToString() {
    return cat().name() + "(???)";
  }
}

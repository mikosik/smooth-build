package org.smoothbuild.bytecode.obj.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.type.base.ExprCatB;
import org.smoothbuild.bytecode.type.base.TypeB;

/**
 * Expression.
 * This class is thread-safe.
 */
public abstract class ExprB extends ObjB {
  public ExprB(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    super(merkleRoot, byteDb);
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
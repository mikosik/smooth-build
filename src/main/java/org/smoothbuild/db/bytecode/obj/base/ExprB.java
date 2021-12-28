package org.smoothbuild.db.bytecode.obj.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.type.base.ExprCatB;
import org.smoothbuild.db.bytecode.type.base.TypeB;

/**
 * Expression.
 * This class is thread-safe.
 */
public abstract class ExprB extends ObjB {
  public ExprB(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
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

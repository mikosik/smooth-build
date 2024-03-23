package org.smoothbuild.virtualmachine.bytecode.expr.value;

import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * Instance of a value.
 * This class is thread-safe.
 */
public abstract sealed class BValue extends BExpr
    permits BFunc, BArray, BBlob, BBool, BInt, BString, BTuple {
  public BValue(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  @Override
  public BType evaluationType() {
    return type();
  }

  public BType type() {
    return (BType) category();
  }
}

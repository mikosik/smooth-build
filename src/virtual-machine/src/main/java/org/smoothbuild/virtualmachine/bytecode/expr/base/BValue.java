package org.smoothbuild.virtualmachine.bytecode.expr.base;

import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * Instance of a value.
 * This class is thread-safe.
 */
public abstract sealed class BValue extends BExpr
    permits BFunc, BArray, BBlob, BBool, BInt, BString, BTuple {
  public BValue(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  @Override
  public BType evaluationType() {
    return type();
  }

  public BType type() {
    return (BType) kind();
  }
}

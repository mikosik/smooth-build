package org.smoothbuild.virtualmachine.bytecode.expr.base;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;

/**
 * This class is immutable.
 */
public final class BBool extends BValue {
  public BBool(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  public boolean toJavaBoolean() throws BytecodeException {
    return readData(() -> hashedDb().readBoolean(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return Boolean.toString(toJavaBoolean());
  }
}
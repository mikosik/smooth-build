package org.smoothbuild.vm.bytecode.expr.value;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;

/**
 * This class is immutable.
 */
public final class BoolB extends ValueB {
  public BoolB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  public boolean toJ() throws BytecodeException {
    return readData(() -> hashedDb().readBoolean(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return Boolean.toString(toJ());
  }
}

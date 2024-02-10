package org.smoothbuild.vm.bytecode.expr.value;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;

/**
 * This class is immutable.
 */
public final class BoolB extends ValueB {
  public BoolB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  public boolean toJ() throws BytecodeException {
    return readData(() -> hashedDb().readBoolean(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return Boolean.toString(toJ());
  }
}

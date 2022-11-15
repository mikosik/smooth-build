package org.smoothbuild.bytecode.expr.inst;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;

/**
 * This class is immutable.
 */
public final class BoolB extends ValueB {
  public BoolB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  public boolean toJ() {
    return readData(() -> hashedDb().readBoolean(dataHash()));
  }

  @Override
  public String exprToString() {
    return Boolean.toString(toJ());
  }
}

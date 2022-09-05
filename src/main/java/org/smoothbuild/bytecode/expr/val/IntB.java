package org.smoothbuild.bytecode.expr.val;

import java.math.BigInteger;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class IntB extends ValB {
  public IntB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  public BigInteger toJ() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String exprToString() {
    return toJ().toString();
  }
}

package org.smoothbuild.bytecode.expr.inst;

import java.math.BigInteger;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class IntB extends ValueB {
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

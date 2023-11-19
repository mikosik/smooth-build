package org.smoothbuild.vm.bytecode.expr.value;

import java.math.BigInteger;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;

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

package org.smoothbuild.vm.bytecode.expr.value;

import java.math.BigInteger;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class IntB extends ValueB {
  public IntB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  public BigInteger toJ() throws BytecodeException {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return toJ().toString();
  }
}

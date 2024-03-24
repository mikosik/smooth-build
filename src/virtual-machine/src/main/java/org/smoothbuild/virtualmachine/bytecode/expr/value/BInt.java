package org.smoothbuild.virtualmachine.bytecode.expr.value;

import java.math.BigInteger;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class BInt extends BValue {
  public BInt(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  public BigInteger toJavaBigInteger() throws BytecodeException {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return toJavaBigInteger().toString();
  }
}

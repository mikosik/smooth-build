package org.smoothbuild.virtualmachine.bytecode.expr.value;

import okio.BufferedSource;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class BBlob extends BValue {
  public BBlob(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  public BufferedSource source() throws BytecodeException {
    return readData(() -> hashedDb().source(dataHash()));
  }

  @Override
  public String exprToString() {
    return "0x??";
  }
}

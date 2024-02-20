package org.smoothbuild.vm.bytecode.expr.value;

import okio.BufferedSource;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class BlobB extends ValueB {
  public BlobB(MerkleRoot merkleRoot, ExprDb exprDb) {
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

package org.smoothbuild.bytecode.expr.inst;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;

import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public final class BlobB extends InstB {
  public BlobB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  public BufferedSource source() {
    return readData(() -> hashedDb().source(dataHash()));
  }

  @Override
  public String exprToString() {
    return "0x??";
  }
}

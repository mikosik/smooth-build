package org.smoothbuild.db.bytecode.obj.val;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;

import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public final class BlobB extends ValB {
  public BlobB(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    super(merkleRoot, byteDb);
  }

  public BufferedSource source() {
    return readData(() -> hashedDb().source(dataHash()));
  }

  @Override
  public String objToString() {
    return "0x??";
  }
}


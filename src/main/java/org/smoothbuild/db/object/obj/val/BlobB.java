package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public final class BlobB extends ValB {
  public BlobB(MerkleRoot merkleRoot, ByteDb byteDb) {
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


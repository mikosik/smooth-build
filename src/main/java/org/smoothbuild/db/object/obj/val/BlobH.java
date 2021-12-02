package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public final class BlobH extends ValH {
  public BlobH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  public BufferedSource source() {
    return readData(() -> hashedDb().source(dataHash()));
  }

  @Override
  public String objToString() {
    return "0x??";
  }
}


package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;

import okio.BufferedSource;

/**
 * This class is immutable.
 */
public class Blob extends Val {
  public Blob(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public BufferedSource source() {
    return readData(() -> hashedDb().source(dataHash()));
  }

  @Override
  public String valueToString() {
    return "0x??";
  }
}


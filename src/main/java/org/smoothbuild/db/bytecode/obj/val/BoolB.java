package org.smoothbuild.db.bytecode.obj.val;

import org.smoothbuild.db.bytecode.obj.ByteDb;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;

/**
 * This class is immutable.
 */
public final class BoolB extends ValB {
  public BoolB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
  }

  public boolean toJ() {
    return readData(() -> hashedDb().readBoolean(dataHash()));
  }

  @Override
  public String objToString() {
    return Boolean.toString(toJ());
  }
}

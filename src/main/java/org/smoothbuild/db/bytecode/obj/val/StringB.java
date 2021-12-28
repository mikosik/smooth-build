package org.smoothbuild.db.bytecode.obj.val;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.bytecode.obj.ByteDb;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class StringB extends ValB {
  public StringB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
  }

  public String toJ() {
    return readData(() -> hashedDb().readString(dataHash()));
  }

  @Override
  public String objToString() {
    return escapedAndLimitedWithEllipsis(toJ(), 30);
  }
}

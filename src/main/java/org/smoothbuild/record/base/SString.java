package org.smoothbuild.record.base;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.db.Helpers;

/**
 * This class is immutable.
 */
public class SString extends RecordImpl {
  public SString(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public String jValue() {
    return Helpers.wrapException(hash(), () -> hashedDb.readString(dataHash()));
  }

  @Override
  public String valueToString() {
    return escapedAndLimitedWithEllipsis(jValue(), 30);
  }
}

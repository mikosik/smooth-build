package org.smoothbuild.db.record.base;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.db.Helpers;

/**
 * This class is immutable.
 */
public class RString extends RecordImpl {
  public RString(MerkleRoot merkleRoot, HashedDb hashedDb) {
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

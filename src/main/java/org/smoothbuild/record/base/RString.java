package org.smoothbuild.record.base;

import static org.smoothbuild.record.db.Helpers.wrapException;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.HashedDb;

/**
 * This class is immutable.
 */
public class RString extends RecordImpl {
  public RString(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public String jValue() {
    return wrapException(hash(), () -> hashedDb.readString(dataHash()));
  }

  @Override
  public String valueToString() {
    return escapedAndLimitedWithEllipsis(jValue(), 30);
  }
}

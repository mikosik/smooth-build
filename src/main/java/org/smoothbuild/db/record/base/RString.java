package org.smoothbuild.db.record.base;

import static org.smoothbuild.db.record.db.Helpers.wrapDecodingRecordException;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.HashedDb;

/**
 * This class is immutable.
 */
public class RString extends Record {
  public RString(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public String jValue() {
    return wrapDecodingRecordException(hash(), () -> hashedDb.readString(dataHash()));
  }

  @Override
  public String valueToString() {
    return escapedAndLimitedWithEllipsis(jValue(), 30);
  }
}

package org.smoothbuild.lang.object.base;

import static org.smoothbuild.lang.object.db.Helpers.wrapException;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.HashedDb;

/**
 * This class is immutable.
 */
public class SString extends SObjectImpl {
  public SString(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public String jValue() {
    return wrapException(hash(), () -> hashedDb.readString(dataHash()));
  }

  @Override
  protected String valueToString() {
    return escapedAndLimitedWithEllipsis(jValue(), 30);
  }
}

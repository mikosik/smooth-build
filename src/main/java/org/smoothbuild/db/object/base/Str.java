package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapDecodingObjectException;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.HashedDb;

/**
 * This class is immutable.
 */
public class Str extends Obj {
  public Str(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public String jValue() {
    return wrapDecodingObjectException(hash(), () -> hashedDb.readString(dataHash()));
  }

  @Override
  public String valueToString() {
    return escapedAndLimitedWithEllipsis(jValue(), 30);
  }
}

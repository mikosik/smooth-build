package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapDecodingObjectException;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class Str extends Val {
  public Str(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public String jValue() {
    return wrapDecodingObjectException(hash(), () -> hashedDb().readString(dataHash()));
  }

  @Override
  public String valueToString() {
    return escapedAndLimitedWithEllipsis(jValue(), 30);
  }
}

package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class StringH extends ValH {
  public StringH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  public String toJ() {
    return readData(() -> hashedDb().readString(dataHash()));
  }

  @Override
  public String objToString() {
    return escapedAndLimitedWithEllipsis(toJ(), 30);
  }
}

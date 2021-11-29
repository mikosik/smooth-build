package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ValueH;

/**
 * This class is immutable.
 */
public class StringH extends ValueH {
  public StringH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
  }

  public String jValue() {
    return readData(() -> hashedDb().readString(dataHash()));
  }

  @Override
  public String valToString() {
    return escapedAndLimitedWithEllipsis(jValue(), 30);
  }
}

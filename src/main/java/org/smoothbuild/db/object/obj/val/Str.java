package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;

/**
 * This class is immutable.
 */
public class Str extends Val {
  public Str(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  public String jValue() {
    return readData(() -> hashedDb().readString(dataHash()));
  }

  @Override
  public String valueToString() {
    return escapedAndLimitedWithEllipsis(jValue(), 30);
  }
}

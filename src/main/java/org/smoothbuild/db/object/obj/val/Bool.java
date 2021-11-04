package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;

/**
 * This class is immutable.
 */
public class Bool extends Val {
  public Bool(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  public boolean jValue() {
    return readData(() -> hashedDb().readBoolean(dataHash()));
  }

  @Override
  public String valueToString() {
    return Boolean.toString(jValue());
  }
}

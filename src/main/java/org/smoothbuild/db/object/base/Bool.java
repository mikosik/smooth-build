package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsDecodeObjException;

import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class Bool extends Val {
  public Bool(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public boolean jValue() {
    return wrapHashedDbExceptionAsDecodeObjException(
        hash(), () -> hashedDb().readBoolean(dataHash()));
  }

  @Override
  public String valueToString() {
    return Boolean.toString(jValue());
  }
}

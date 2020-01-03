package org.smoothbuild.lang.object.base;

import static org.smoothbuild.lang.object.db.Helpers.wrapException;

import org.smoothbuild.db.hashed.HashedDb;

/**
 * This class is immutable.
 */
public class Bool extends SObjectImpl {
  public Bool(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public boolean jValue() {
    return wrapException(hash(), () -> hashedDb.readBoolean(dataHash()));
  }

  @Override
  public String toString() {
    return type().name() + "(" + jValue() + "):" + hash();
  }
}

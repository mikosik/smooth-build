package org.smoothbuild.lang.object.base;

import static org.smoothbuild.lang.object.db.Helpers.wrapException;

import org.smoothbuild.db.hashed.HashedDb;

public class Bool extends SObjectImpl {
  public Bool(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public boolean data() {
    return wrapException(hash(), () -> hashedDb.readBoolean(dataHash()));
  }

  @Override
  public String toString() {
    return type().name() + "(" + data() + "):" + hash();
  }
}

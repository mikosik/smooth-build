package org.smoothbuild.lang.object.base;

import static org.smoothbuild.lang.object.db.Helpers.wrapException;

import org.smoothbuild.db.hashed.HashedDb;

public class SString extends SObjectImpl {
  public SString(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public String jValue() {
    return wrapException(hash(), () -> hashedDb.readString(dataHash()));
  }
}

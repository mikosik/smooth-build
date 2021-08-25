package org.smoothbuild.db.object.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.db.hashed.HashedDb;

/**
 * This class is immutable.
 */
public abstract class Val extends Obj {
  protected final HashedDb hashedDb;

  public Val(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot);
    this.hashedDb = requireNonNull(hashedDb);
  }
}

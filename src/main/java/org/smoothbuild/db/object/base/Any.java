package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapDecodingObjectException;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;

/**
 * This class is immutable.
 */
public class Any extends Obj {
  public Any(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public Hash wrappedHash() {
    List<Hash> hash = wrapDecodingObjectException(hash(), () -> hashedDb.readHashes(dataHash(), 1));
    return hash.get(0);
  }

  @Override
  public String valueToString() {
    return "Any??";
  }
}


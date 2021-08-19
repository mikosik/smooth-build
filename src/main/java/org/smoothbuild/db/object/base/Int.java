package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapDecodingObjectException;

import java.math.BigInteger;

import org.smoothbuild.db.hashed.HashedDb;

/**
 * This class is immutable.
 */
public class Int extends Obj {
  public Int(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public BigInteger jValue() {
    return wrapDecodingObjectException(hash(), () -> hashedDb.readBigInteger(dataHash()));
  }

  @Override
  public String valueToString() {
    return jValue().toString();
  }
}

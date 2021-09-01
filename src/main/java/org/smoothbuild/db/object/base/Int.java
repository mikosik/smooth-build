package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsDecodeObjException;

import java.math.BigInteger;

import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class Int extends Val {
  public Int(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public BigInteger jValue() {
    return wrapHashedDbExceptionAsDecodeObjException(
        hash(),
        () -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String valueToString() {
    return jValue().toString();
  }
}

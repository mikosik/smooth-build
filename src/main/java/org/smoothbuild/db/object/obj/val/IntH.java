package org.smoothbuild.db.object.obj.val;

import java.math.BigInteger;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ValueH;

/**
 * This class is immutable.
 */
public class IntH extends ValueH {
  public IntH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
  }

  public BigInteger toJ() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String valToString() {
    return toJ().toString();
  }
}

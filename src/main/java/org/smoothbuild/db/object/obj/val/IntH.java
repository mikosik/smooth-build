package org.smoothbuild.db.object.obj.val;

import java.math.BigInteger;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class IntH extends ValH {
  public IntH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  public BigInteger toJ() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String objToString() {
    return toJ().toString();
  }
}

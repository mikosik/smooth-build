package org.smoothbuild.db.bytecode.obj.val;

import java.math.BigInteger;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class IntB extends ValB {
  public IntB(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    super(merkleRoot, byteDb);
  }

  public BigInteger toJ() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String objToString() {
    return toJ().toString();
  }
}

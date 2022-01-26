package org.smoothbuild.bytecode.obj.val;

import java.math.BigInteger;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class IntB extends ValB {
  public IntB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
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

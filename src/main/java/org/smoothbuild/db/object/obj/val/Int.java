package org.smoothbuild.db.object.obj.val;

import java.math.BigInteger;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;

/**
 * This class is immutable.
 */
public class Int extends Val {
  public Int(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  public BigInteger jValue() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String valueToString() {
    return jValue().toString();
  }
}

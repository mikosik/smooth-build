package org.smoothbuild.db.object.obj.expr;

import java.math.BigInteger;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

/**
 * Parameter reference.
 * This class is thread-safe.
 */
public class ParamRefH extends ExprH {
  public ParamRefH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  public BigInteger value() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String objToString() {
    return cat().name() + "(" + value() + ")";
  }
}

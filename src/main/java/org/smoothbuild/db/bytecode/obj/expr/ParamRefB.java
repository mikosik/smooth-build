package org.smoothbuild.db.bytecode.obj.expr;

import java.math.BigInteger;

import org.smoothbuild.db.bytecode.obj.ByteDb;
import org.smoothbuild.db.bytecode.obj.base.ExprB;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;

/**
 * Parameter reference.
 * This class is thread-safe.
 */
public class ParamRefB extends ExprB {
  public ParamRefB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
  }

  public BigInteger value() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String objToString() {
    return cat().name() + "(" + value() + ")";
  }
}

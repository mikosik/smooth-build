package org.smoothbuild.bytecode.expr.oper;

import java.math.BigInteger;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;

/**
 * Parameter reference.
 * This class is thread-safe.
 */
public class ParamRefB extends OperB {
  public ParamRefB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  public BigInteger value() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String exprToString() {
    return category().name() + "(" + value() + ")";
  }
}

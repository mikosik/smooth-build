package org.smoothbuild.vm.bytecode.expr.oper;

import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;

import com.google.common.collect.ImmutableList;

/**
 * Reference to environment value.
 * This class is thread-safe.
 */
public class RefB extends OperB {
  public RefB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  @Override
  public ImmutableList<ExprB> dataSeq() {
    return list();
  }

  public BigInteger value() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String exprToString() {
    return category().name() + "(" + value() + ")";
  }
}

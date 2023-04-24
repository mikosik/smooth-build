package org.smoothbuild.vm.bytecode.expr.oper;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.IntB;

/**
 * Reference to environment value.
 * This class is thread-safe.
 */
public class ReferenceB extends OperB {
  public ReferenceB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  @Override
  public ReferenceSubExprsB subExprs() {
    return new ReferenceSubExprsB();
  }

  public IntB index() {
    return readData(IntB.class);
  }

  @Override
  public String exprToString() {
    return category().name() + "(" + index().toJ() + ")";
  }
}

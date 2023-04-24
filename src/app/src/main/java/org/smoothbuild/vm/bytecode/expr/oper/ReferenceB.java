package org.smoothbuild.vm.bytecode.expr.oper;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.IntB;

import com.google.common.collect.ImmutableList;

/**
 * Reference to environment value.
 * This class is thread-safe.
 */
public class ReferenceB extends OperB {
  public ReferenceB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  @Override
  public ImmutableList<ExprB> dataSeq() {
    return list();
  }

  public IntB index() {
    return readData(IntB.class);
  }

  @Override
  public String exprToString() {
    return category().name() + "(" + index().toJ() + ")";
  }
}

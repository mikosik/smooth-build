package org.smoothbuild.vm.bytecode.expr.oper;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.IntB;

/**
 * Variable that references bound value using De Bruijn indexing with zero-based numbering.
 * <a href="https://en.wikipedia.org/wiki/De_Bruijn_index">De Bruijn index article</a> in wikipedia.
 * <p>
 * This class is thread-safe.
 */
public class VarB extends OperB {
  public VarB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  @Override
  public VarSubExprsB subExprs() {
    return new VarSubExprsB();
  }

  public IntB index() throws BytecodeException {
    return readData(IntB.class);
  }

  @Override
  public String exprToString() throws BytecodeException {
    return category().name() + "(" + index().toJ() + ")";
  }
}

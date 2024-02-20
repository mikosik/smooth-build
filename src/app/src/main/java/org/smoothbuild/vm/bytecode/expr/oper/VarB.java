package org.smoothbuild.vm.bytecode.expr.oper;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.IntB;

/**
 * Variable that references bound value using De Bruijn indexing with zero-based numbering.
 * <a href="https://en.wikipedia.org/wiki/De_Bruijn_index">De Bruijn index article</a> in wikipedia.
 * <p>
 * This class is thread-safe.
 */
public class VarB extends OperB {
  public VarB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  @Override
  public SubExprsB subExprs() {
    return new SubExprsB();
  }

  public IntB index() throws BytecodeException {
    return readData(IntB.class);
  }

  @Override
  public String exprToString() throws BytecodeException {
    return category().name() + "(" + index().toJ() + ")";
  }

  public static record SubExprsB() implements ExprsB {
    @Override
    public List<ExprB> toList() {
      return list();
    }
  }
}

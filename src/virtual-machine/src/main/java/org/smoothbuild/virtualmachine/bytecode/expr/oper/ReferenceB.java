package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;

/**
 * References bound value using De Bruijn indexing with zero-based numbering.
 * <a href="https://en.wikipedia.org/wiki/De_Bruijn_index">De Bruijn index article</a> in wikipedia.
 * <p>
 * This class is thread-safe.
 */
public class ReferenceB extends OperB {
  public ReferenceB(MerkleRoot merkleRoot, ExprDb exprDb) {
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
    return category().name() + "(" + index().toJavaBigInteger() + ")";
  }

  public static record SubExprsB() implements ExprsB {
    @Override
    public List<ExprB> toList() {
      return list();
    }
  }
}

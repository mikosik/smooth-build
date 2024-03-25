package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;

/**
 * References bound value using De Bruijn indexing with zero-based numbering.
 * <a href="https://en.wikipedia.org/wiki/De_Bruijn_index">De Bruijn index article</a> in wikipedia.
 * <p>
 * This class is thread-safe.
 */
public final class BReference extends BOper {
  public BReference(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  @Override
  public SubExprsB subExprs() {
    return new SubExprsB();
  }

  public BInt index() throws BytecodeException {
    return readData(BInt.class);
  }

  @Override
  public String exprToString() throws BytecodeException {
    return kind().name() + ":" + evaluationType() + "(" + index().toJavaBigInteger() + ")";
  }

  public static record SubExprsB() implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list();
    }
  }
}

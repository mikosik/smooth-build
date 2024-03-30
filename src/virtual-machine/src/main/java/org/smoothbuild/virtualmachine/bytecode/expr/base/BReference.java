package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BReferenceKind;

/**
 * References bound value using De Bruijn indexing with zero-based numbering.
 * <a href="https://en.wikipedia.org/wiki/De_Bruijn_index">De Bruijn index article</a> in wikipedia.
 * <p>
 * This class is thread-safe.
 */
public final class BReference extends BOperation {
  public BReference(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BReferenceKind);
  }

  @Override
  public BSubExprs subExprs() {
    return new BSubExprs();
  }

  public BInt index() throws BytecodeException {
    return readData(BInt.class);
  }

  @Override
  public String exprToString() throws BytecodeException {
    return kind().name() + ":" + evaluationType() + "(" + index().toJavaBigInteger() + ")";
  }

  public static record BSubExprs() implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list();
    }
  }
}

package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BOrderCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;

/**
 * This class is thread-safe.
 */
public class BOrder extends BOper {
  public BOrder(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof BOrderCategory);
  }

  @Override
  public BOrderCategory category() {
    return (BOrderCategory) super.category();
  }

  @Override
  public BArrayType evaluationType() {
    return category().evaluationType();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    return new SubExprsB(elements());
  }

  public List<BExpr> elements() throws BytecodeException {
    var elements = readDataAsExprChain(BExpr.class);
    var expectedElementT = category().evaluationType().elem();
    for (int i = 0; i < elements.size(); i++) {
      var actualT = elements.get(i).evaluationType();
      if (!expectedElementT.equals(actualT)) {
        throw new DecodeExprWrongNodeTypeException(
            hash(), category(), "elements", i, expectedElementT, actualT);
      }
    }
    return elements;
  }

  public static record SubExprsB(List<BExpr> elements) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return elements;
    }
  }
}

package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BOrderKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;

/**
 * This class is thread-safe.
 */
public final class BOrder extends BOper {
  public BOrder(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BOrderKind);
  }

  @Override
  public BOrderKind kind() {
    return (BOrderKind) super.kind();
  }

  @Override
  public BArrayType evaluationType() {
    return kind().evaluationType();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    return new SubExprsB(elements());
  }

  public List<BExpr> elements() throws BytecodeException {
    var elements = readDataAsExprChain(BExpr.class);
    var expectedElementT = kind().evaluationType().elem();
    for (int i = 0; i < elements.size(); i++) {
      var actualT = elements.get(i).evaluationType();
      if (!expectedElementT.equals(actualT)) {
        throw new DecodeExprWrongNodeTypeException(
            hash(), kind(), "elements", i, expectedElementT, actualT);
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

package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OrderCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;

/**
 * This class is thread-safe.
 */
public class OrderB extends OperB {
  public OrderB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof OrderCB);
  }

  @Override
  public OrderCB category() {
    return (OrderCB) super.category();
  }

  @Override
  public ArrayTB evaluationType() {
    return category().evaluationType();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    return new SubExprsB(elements());
  }

  public List<ExprB> elements() throws BytecodeException {
    var elements = readDataAsExprChain(ExprB.class);
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

  public static record SubExprsB(List<ExprB> elements) implements ExprsB {
    @Override
    public List<ExprB> toList() {
      return elements;
    }
  }
}

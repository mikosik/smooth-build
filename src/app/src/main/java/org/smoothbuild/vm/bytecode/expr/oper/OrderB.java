package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.vm.bytecode.type.oper.OrderCB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;

/**
 * This class is thread-safe.
 */
public class OrderB extends OperB {
  public OrderB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof OrderCB);
  }

  @Override
  public OrderCB category() {
    return (OrderCB) super.category();
  }

  @Override
  public ArrayTB evaluationT() {
    return category().evaluationT();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    return new SubExprsB(elements());
  }

  public List<ExprB> elements() throws BytecodeException {
    var elements = readDataSeqElems(ExprB.class);
    var expectedElementT = category().evaluationT().elem();
    for (int i = 0; i < elements.size(); i++) {
      var actualT = elements.get(i).evaluationT();
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

package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.vm.bytecode.type.oper.OrderCB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;

import com.google.common.collect.ImmutableList;

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
  public ImmutableList<ExprB> dataSeq() {
    var elems = readDataSeqElems(ExprB.class);
    var expectedElemT = category().evaluationT().elem();
    for (int i = 0; i < elems.size(); i++) {
      var actualT = elems.get(i).evaluationT();
      if (!expectedElemT.equals(actualT)) {
        throw new DecodeExprWrongNodeTypeExc(hash(), category(), "elems", i, expectedElemT, actualT);
      }
    }
    return elems;
  }
}

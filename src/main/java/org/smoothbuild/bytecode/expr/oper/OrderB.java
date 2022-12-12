package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.oper.OrderCB;
import org.smoothbuild.bytecode.type.value.ArrayTB;

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
  public ArrayTB evalT() {
    return category().evalT();
  }

  @Override
  public ImmutableList<ExprB> dataSeq() {
    var elems = readDataSeqElems(ExprB.class);
    var expectedElemT = category().evalT().elem();
    for (int i = 0; i < elems.size(); i++) {
      var actualT = elems.get(i).evalT();
      if (!expectedElemT.equals(actualT)) {
        throw new DecodeExprWrongNodeTypeExc(hash(), category(), "elems", i, expectedElemT, actualT);
      }
    }
    return elems;
  }
}

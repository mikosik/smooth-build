package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.oper.OrderCB;
import org.smoothbuild.bytecode.type.val.ArrayTB;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class OrderB extends OperB {
  public OrderB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.cat() instanceof OrderCB);
  }

  @Override
  public OrderCB cat() {
    return (OrderCB) super.cat();
  }

  @Override
  public ArrayTB type() {
    return cat().evalT();
  }

  public ImmutableList<ExprB> elems() {
    var elems = readSeqExprs(DATA_PATH, dataHash(), ExprB.class);
    var expectedElemT = cat().evalT().elem();
    for (int i = 0; i < elems.size(); i++) {
      var actualT = elems.get(i).type();
      if (!expectedElemT.equals(actualT)) {
        throw new DecodeExprWrongNodeTypeExc(hash(), cat(), "elems", i, expectedElemT, actualT);
      }
    }
    return elems;
  }
}

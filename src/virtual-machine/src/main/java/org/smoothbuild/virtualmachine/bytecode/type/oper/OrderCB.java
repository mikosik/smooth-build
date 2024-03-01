package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.ORDER;

import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class OrderCB extends OperCB {
  public OrderCB(Hash hash, TypeB evaluationT) {
    super(hash, ORDER, evaluationT);
    checkArgument(evaluationT instanceof ArrayTB);
  }

  @Override
  public ArrayTB evaluationT() {
    return (ArrayTB) super.evaluationT();
  }

  @Override
  public OrderB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof OrderCB);
    return new OrderB(merkleRoot, exprDb);
  }
}
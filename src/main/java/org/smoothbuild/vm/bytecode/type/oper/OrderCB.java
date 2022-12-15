package org.smoothbuild.vm.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.ORDER;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class OrderCB extends OperCB {
  public OrderCB(Hash hash, TypeB evalT) {
    super(hash, ORDER, evalT);
    checkArgument(evalT instanceof ArrayTB);
  }

  @Override
  public ArrayTB evalT() {
    return (ArrayTB) super.evalT();
  }

  @Override
  public OrderB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof OrderCB);
    return new OrderB(merkleRoot, bytecodeDb);
  }
}

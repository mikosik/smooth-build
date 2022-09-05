package org.smoothbuild.bytecode.type.oper;

import static org.smoothbuild.bytecode.type.CatKindB.ORDER;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class OrderCB extends OperCatB {
  public OrderCB(Hash hash, ArrayTB evalT) {
    super(hash, "Order", ORDER, evalT);
  }

  @Override
  public ArrayTB evalT() {
    return (ArrayTB) super.evalT();
  }

  @Override
  public OrderB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (OrderB) super.newObj(merkleRoot, bytecodeDb);
  }
}

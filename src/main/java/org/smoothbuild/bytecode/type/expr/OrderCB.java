package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.base.CatKindB.ORDER;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.type.base.ExprCatB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class OrderCB extends ExprCatB {
  public OrderCB(Hash hash, ArrayTB evalT) {
    super(hash, "Order", ORDER, evalT);
  }

  @Override
  public ArrayTB evalT() {
    return (ArrayTB) super.evalT();
  }

  @Override
  public OrderB newObj(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    return (OrderB) super.newObj(merkleRoot, byteDb);
  }
}

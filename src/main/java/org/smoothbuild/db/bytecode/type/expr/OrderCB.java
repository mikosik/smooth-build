package org.smoothbuild.db.bytecode.type.expr;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.ORDER;

import org.smoothbuild.db.bytecode.obj.ByteDb;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.expr.OrderB;
import org.smoothbuild.db.bytecode.type.base.ExprCatB;
import org.smoothbuild.db.bytecode.type.val.ArrayTB;
import org.smoothbuild.db.hashed.Hash;

/**
 * This class is immutable.
 */
public class OrderCB extends ExprCatB {
  public OrderCB(Hash hash, ArrayTB evalT) {
    super("Order", hash, ORDER, evalT);
  }

  @Override
  public ArrayTB evalT() {
    return (ArrayTB) super.evalT();
  }

  @Override
  public OrderB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (OrderB) super.newObj(merkleRoot, byteDb);
  }
}

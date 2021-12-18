package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindB.ORDER;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.OrderB;
import org.smoothbuild.db.object.type.base.ExprCatB;
import org.smoothbuild.db.object.type.val.ArrayTB;

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

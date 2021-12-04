package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindH.ORDER;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.type.base.ExprCatH;
import org.smoothbuild.db.object.type.val.ArrayTH;

/**
 * This class is immutable.
 */
public class OrderCH extends ExprCatH {
  public OrderCH(Hash hash, ArrayTH evalT) {
    super("Order", hash, ORDER, evalT);
  }

  @Override
  public ArrayTH evalT() {
    return (ArrayTH) super.evalT();
  }

  @Override
  public OrderH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (OrderH) super.newObj(merkleRoot, objDb);
  }
}

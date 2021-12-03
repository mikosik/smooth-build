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
  public OrderCH(Hash hash, ArrayTH evalType) {
    super("Order", hash, ORDER, evalType);
  }

  @Override
  public ArrayTH evalType() {
    return (ArrayTH) super.evalType();
  }

  @Override
  public OrderH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (OrderH) super.newObj(merkleRoot, objDb);
  }
}

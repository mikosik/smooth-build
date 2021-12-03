package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.SpecKindH.ORDER;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.type.base.ExprSpecH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;

/**
 * This class is immutable.
 */
public class OrderTypeH extends ExprSpecH {
  public OrderTypeH(Hash hash, ArrayTypeH evalType) {
    super("Order", hash, ORDER, evalType);
  }

  @Override
  public ArrayTypeH evalType() {
    return (ArrayTypeH) super.evalType();
  }

  @Override
  public OrderH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (OrderH) super.newObj(merkleRoot, objDb);
  }
}

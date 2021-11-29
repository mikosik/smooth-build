package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.SpecKindH.ORDER;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.type.base.ExprSpecH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;

/**
 * This class is immutable.
 */
public class OrderTypeH extends ExprSpecH {
  public OrderTypeH(Hash hash, ArrayTypeH evaluationType) {
    super("ORDER", hash, ORDER, evaluationType);
  }

  @Override
  public ArrayTypeH evalType() {
    return (ArrayTypeH) super.evalType();
  }

  @Override
  public OrderH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (OrderH) super.newObj(merkleRoot, objectHDb);
  }
}

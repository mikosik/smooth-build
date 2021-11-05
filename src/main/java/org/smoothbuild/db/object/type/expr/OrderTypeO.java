package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.ORDER;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Order;
import org.smoothbuild.db.object.type.base.TypeE;
import org.smoothbuild.db.object.type.val.ArrayTypeO;

/**
 * This class is immutable.
 */
public class OrderTypeO extends TypeE {
  public OrderTypeO(Hash hash, ArrayTypeO evaluationType) {
    super("ORDER", hash, ORDER, evaluationType);
  }

  @Override
  public ArrayTypeO evaluationType() {
    return (ArrayTypeO) super.evaluationType();
  }

  @Override
  public Order newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Order(merkleRoot, objDb);
  }
}

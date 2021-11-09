package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.TypeKindH.ORDER;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.val.ArrayTypeH;

/**
 * This class is immutable.
 */
public class OrderTypeH extends TypeHE {
  public OrderTypeH(Hash hash, ArrayTypeH evaluationType) {
    super("ORDER", hash, ORDER, evaluationType);
  }

  @Override
  public ArrayTypeH evaluationType() {
    return (ArrayTypeH) super.evaluationType();
  }

  @Override
  public OrderH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new OrderH(merkleRoot, objectHDb);
  }
}

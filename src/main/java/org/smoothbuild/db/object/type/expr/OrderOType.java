package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.ORDER;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Order;
import org.smoothbuild.db.object.type.base.ExprType;
import org.smoothbuild.db.object.type.val.ArrayOType;

/**
 * This class is immutable.
 */
public class OrderOType extends ExprType {
  public OrderOType(Hash hash, ArrayOType evaluationType) {
    super("ORDER", hash, ORDER, evaluationType);
  }

  @Override
  public ArrayOType evaluationType() {
    return (ArrayOType) super.evaluationType();
  }

  @Override
  public Order newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Order(merkleRoot, objectDb);
  }
}

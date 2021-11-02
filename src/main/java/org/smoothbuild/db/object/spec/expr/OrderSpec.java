package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.ORDER;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Order;
import org.smoothbuild.db.object.spec.base.ExprSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;

/**
 * This class is immutable.
 */
public class OrderSpec extends ExprSpec {
  public OrderSpec(Hash hash, ArraySpec evaluationSpec) {
    super("ORDER", hash, ORDER, evaluationSpec);
  }

  @Override
  public ArraySpec evaluationSpec() {
    return (ArraySpec) super.evaluationSpec();
  }

  @Override
  public Order newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Order(merkleRoot, objectDb);
  }
}

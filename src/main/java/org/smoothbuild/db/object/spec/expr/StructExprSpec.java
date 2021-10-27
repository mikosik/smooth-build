package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRUCT_EXPR;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.StructExpr;
import org.smoothbuild.db.object.spec.base.ExprSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;

/**
 * This class is immutable.
 */
public class StructExprSpec extends ExprSpec {
  public StructExprSpec(Hash hash, StructSpec evaluationSpec) {
    super("STRUCT", hash, STRUCT_EXPR, evaluationSpec);
  }

  @Override
  public StructSpec evaluationSpec() {
    return (StructSpec) super.evaluationSpec();
  }

  @Override
  public StructExpr newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new StructExpr(merkleRoot, objectDb);
  }
}

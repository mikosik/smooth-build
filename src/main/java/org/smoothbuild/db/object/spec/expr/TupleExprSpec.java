package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.TUPLE_EXPR;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.TupleExpr;
import org.smoothbuild.db.object.spec.base.ExprSpec;
import org.smoothbuild.db.object.spec.val.TupleSpec;

/**
 * This class is immutable.
 */
public class TupleExprSpec extends ExprSpec {
  public TupleExprSpec(Hash hash, TupleSpec evaluationSpec) {
    super("TUPLE", hash, TUPLE_EXPR, evaluationSpec);
  }

  @Override
  public TupleSpec evaluationSpec() {
    return (TupleSpec) super.evaluationSpec();
  }

  @Override
  public TupleExpr newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new TupleExpr(merkleRoot, objectDb);
  }
}

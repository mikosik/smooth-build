package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD_EXPR;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.RecExpr;
import org.smoothbuild.db.object.spec.base.ExprSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;

/**
 * This class is immutable.
 */
public class RecExprSpec extends ExprSpec {
  public RecExprSpec(Hash hash, RecSpec evaluationSpec) {
    super("RECORD", hash, RECORD_EXPR, evaluationSpec);
  }

  @Override
  public RecSpec evaluationSpec() {
    return (RecSpec) super.evaluationSpec();
  }

  @Override
  public RecExpr newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new RecExpr(merkleRoot, objectDb);
  }
}

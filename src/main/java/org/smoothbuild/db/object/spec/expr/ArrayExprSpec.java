package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY_EXPR;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.ArrayExpr;
import org.smoothbuild.db.object.spec.base.ExprSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;

/**
 * This class is immutable.
 */
public class ArrayExprSpec extends ExprSpec {
  public ArrayExprSpec(Hash hash, ArraySpec evaluationSpec) {
    super("ARRAY", hash, ARRAY_EXPR, evaluationSpec);
  }

  @Override
  public ArraySpec evaluationSpec() {
    return (ArraySpec) super.evaluationSpec();
  }

  @Override
  public ArrayExpr newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new ArrayExpr(merkleRoot, objectDb);
  }
}

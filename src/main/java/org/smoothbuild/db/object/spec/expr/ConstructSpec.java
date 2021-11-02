package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONSTRUCT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Construct;
import org.smoothbuild.db.object.spec.base.ExprSpec;
import org.smoothbuild.db.object.spec.val.TupleSpec;

/**
 * This class is immutable.
 */
public class ConstructSpec extends ExprSpec {
  public ConstructSpec(Hash hash, TupleSpec evaluationSpec) {
    super("CONSTRUCT", hash, CONSTRUCT, evaluationSpec);
  }

  @Override
  public TupleSpec evaluationSpec() {
    return (TupleSpec) super.evaluationSpec();
  }

  @Override
  public Construct newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Construct(merkleRoot, objectDb);
  }
}

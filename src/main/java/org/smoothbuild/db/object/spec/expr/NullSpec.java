package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.NULL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.spec.base.ExprSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;

/**
 * This class is immutable.
 */
public class NullSpec extends ExprSpec {
  public NullSpec(Hash hash, NothingSpec evaluationSpec) {
    super(hash, NULL, evaluationSpec);
  }

  @Override
  public Null newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Null(merkleRoot, objectDb);
  }
}

package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.EARRAY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.EArray;
import org.smoothbuild.db.object.spec.base.ExprSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;

/**
 * This class is immutable.
 */
public class EArraySpec extends ExprSpec {
  public EArraySpec(Hash hash, ArraySpec evaluationSpec) {
    super(hash, EARRAY, evaluationSpec);
  }

  @Override
  public ArraySpec evaluationSpec() {
    return (ArraySpec) super.evaluationSpec();
  }

  @Override
  public EArray newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new EArray(merkleRoot, objectDb);
  }
}

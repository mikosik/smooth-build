package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.ERECORD;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.ERec;
import org.smoothbuild.db.object.spec.base.ExprSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;

/**
 * This class is immutable.
 */
public class ERecSpec extends ExprSpec {
  public ERecSpec(Hash hash, RecSpec evaluationSpec) {
    super(hash, ERECORD, evaluationSpec);
  }

  @Override
  public RecSpec evaluationSpec() {
    return (RecSpec) super.evaluationSpec();
  }

  @Override
  public ERec newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new ERec(merkleRoot, objectDb);
  }
}

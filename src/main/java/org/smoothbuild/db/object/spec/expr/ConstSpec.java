package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONST;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.spec.base.ExprSpec;

/**
 * This class is immutable.
 */
public class ConstSpec extends ExprSpec {
  public ConstSpec(Hash hash, ObjectDb objectDb) {
    super(hash, CONST, objectDb);
  }

  @Override
  public Const newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Const(merkleRoot, objectDb());
  }
}

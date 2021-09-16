package org.smoothbuild.db.object.spec.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.spec.base.ExprSpec;

/**
 * This class is immutable.
 */
public class CallSpec extends ExprSpec {
  public CallSpec(Hash hash) {
    super(hash, CALL);
  }

  @Override
  public Call newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Call(merkleRoot, objectDb);
  }
}

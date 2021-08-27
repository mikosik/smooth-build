package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.SpecKind.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Call;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class CallSpec extends ExprSpec {
  public CallSpec(Hash hash, ObjectDb objectDb) {
    super(hash, CALL, objectDb);
  }

  @Override
  public Call newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Call(merkleRoot, objectDb());
  }
}

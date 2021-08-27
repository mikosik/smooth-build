package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.SpecKind.EARRAY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.EArray;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class EArraySpec extends ExprSpec {
  public EArraySpec(Hash hash, ObjectDb objectDb) {
    super(hash, EARRAY, objectDb);
  }

  @Override
  public EArray newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new EArray(merkleRoot, objectDb());
  }
}

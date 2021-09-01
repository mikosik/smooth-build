package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.SpecKind.INT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Int;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class IntSpec extends ValSpec {
  public IntSpec(Hash hash, ObjectDb objectDb) {
    super(hash, INT, objectDb);
  }

  @Override
  public Int newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Int(merkleRoot, objectDb());
  }
}
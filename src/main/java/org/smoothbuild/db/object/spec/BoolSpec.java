package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.SpecKind.BOOL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class BoolSpec extends ValSpec {
  public BoolSpec(Hash hash, ObjectDb objectDb) {
    super(hash, BOOL, objectDb);
  }

  @Override
  public Bool newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Bool(merkleRoot, objectDb());
  }
}

package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.SpecKind.ANY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.base.Any;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class AnySpec extends Spec {
  public AnySpec(Hash hash, HashedDb hashedDb, ObjectDb objectDb) {
    super(hash, ANY, hashedDb, objectDb);
  }

  @Override
  public Any newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Any(merkleRoot, hashedDb);
  }
}

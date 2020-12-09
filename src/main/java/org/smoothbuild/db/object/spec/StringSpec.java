package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.SpecKind.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class StringSpec extends Spec {
  public StringSpec(Hash hash, HashedDb hashedDb, ObjectDb objectDb) {
    super(hash, STRING, hashedDb, objectDb);
  }

  @Override
  public Str newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Str(merkleRoot, hashedDb);
  }
}

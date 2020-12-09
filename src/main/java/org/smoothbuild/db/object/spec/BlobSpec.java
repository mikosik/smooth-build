package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.SpecKind.BLOB;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class BlobSpec extends Spec {
  public BlobSpec(Hash hash, HashedDb hashedDb, ObjectDb objectDb) {
    super(hash, BLOB, hashedDb, objectDb);
  }

  @Override
  public Blob newObj(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Blob(merkleRoot, hashedDb);
  }
}

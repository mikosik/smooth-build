package org.smoothbuild.db.record.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.record.spec.SpecKind.BLOB;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.MerkleRoot;
import org.smoothbuild.db.record.db.RecordDb;

/**
 * This class is immutable.
 */
public class BlobSpec extends Spec {
  public BlobSpec(Hash hash, HashedDb hashedDb, RecordDb recordDb) {
    super(hash, BLOB, hashedDb, recordDb);
  }

  @Override
  public Blob newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Blob(merkleRoot, hashedDb);
  }
}

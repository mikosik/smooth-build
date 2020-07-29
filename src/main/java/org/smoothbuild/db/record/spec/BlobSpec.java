package org.smoothbuild.db.record.spec;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.MerkleRoot;
import org.smoothbuild.db.record.db.RecordDb;

/**
 * This class is immutable.
 */
public class BlobSpec extends Spec {
  public BlobSpec(MerkleRoot merkleRoot, HashedDb hashedDb, RecordDb recordDb) {
    super(merkleRoot, SpecKind.BLOB, hashedDb, recordDb);
  }

  @Override
  public Blob newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Blob(merkleRoot, hashedDb);
  }
}

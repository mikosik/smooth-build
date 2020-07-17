package org.smoothbuild.record.type;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.MerkleRoot;
import org.smoothbuild.record.db.ObjectDb;

/**
 * This class is immutable.
 */
public class BlobType extends BinaryType {
  public BlobType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, TypeKind.BLOB, hashedDb, objectDb);
  }

  @Override
  public Blob newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Blob(merkleRoot, hashedDb);
  }
}

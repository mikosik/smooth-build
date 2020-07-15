package org.smoothbuild.lang.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.object.type.TypeKind.BLOB;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class BlobType extends ConcreteType {
  public BlobType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, BLOB, Blob.class, hashedDb, objectDb);
  }

  @Override
  public Blob newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Blob(merkleRoot, hashedDb);
  }
}

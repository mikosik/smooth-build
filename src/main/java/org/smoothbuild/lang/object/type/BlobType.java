package org.smoothbuild.lang.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.object.type.TypeNames.BLOB;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.db.ObjectsDb;

public class BlobType extends ConcreteType {
  public BlobType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectsDb objectsDb) {
    super(merkleRoot, null, BLOB, Blob.class, hashedDb, objectsDb);
  }

  @Override
  public Blob newObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Blob(merkleRoot, hashedDb);
  }
}

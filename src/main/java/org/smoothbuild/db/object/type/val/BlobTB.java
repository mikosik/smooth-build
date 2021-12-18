package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindB.BLOB;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.BlobB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BlobTB extends TypeB {
  public BlobTB(Hash hash) {
    super(TypeNames.BLOB, hash, BLOB);
  }

  @Override
  public BlobB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (BlobB) super.newObj(merkleRoot, byteDb);
  }
}

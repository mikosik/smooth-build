package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.TypeKindH.BLOB;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BlobTypeH extends TypeHV {
  public BlobTypeH(Hash hash) {
    super(TypeNames.BLOB, hash, BLOB);
  }

  @Override
  public BlobH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new BlobH(merkleRoot, objectHDb);
  }
}

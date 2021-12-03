package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.BLOB;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BlobTH extends TypeH {
  public BlobTH(Hash hash) {
    super(TypeNames.BLOB, hash, BLOB);
  }

  @Override
  public BlobH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (BlobH) super.newObj(merkleRoot, objDb);
  }
}

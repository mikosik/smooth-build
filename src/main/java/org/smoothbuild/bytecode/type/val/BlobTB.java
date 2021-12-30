package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.obj.ByteDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BlobTB extends TypeB {
  public BlobTB(Hash hash) {
    super(TypeNames.BLOB, hash, CatKindB.BLOB);
  }

  @Override
  public BlobB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (BlobB) super.newObj(merkleRoot, byteDb);
  }
}

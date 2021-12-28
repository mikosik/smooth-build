package org.smoothbuild.db.bytecode.type.val;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.BLOB;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.val.BlobB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BlobTB extends TypeB {
  public BlobTB(Hash hash) {
    super(TypeNames.BLOB, hash, BLOB);
  }

  @Override
  public BlobB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (BlobB) super.newObj(merkleRoot, byteDb);
  }
}

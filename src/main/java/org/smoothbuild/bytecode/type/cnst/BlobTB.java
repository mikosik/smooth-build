package org.smoothbuild.bytecode.type.cnst;

import static org.smoothbuild.bytecode.type.CatKindB.BLOB;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.cnst.BlobB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class BlobTB extends BaseTB {
  public BlobTB(Hash hash) {
    super(hash, TNamesB.BLOB, BLOB);
  }

  @Override
  public BlobB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (BlobB) super.newObj(merkleRoot, objDb);
  }
}

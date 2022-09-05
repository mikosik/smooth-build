package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.BLOB;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.hashed.Hash;

/**
 * This class is immutable.
 */
public class BlobTB extends BaseTB {
  public BlobTB(Hash hash) {
    super(hash, TNamesB.BLOB, BLOB);
  }

  @Override
  public BlobB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (BlobB) super.newObj(merkleRoot, bytecodeDb);
  }
}

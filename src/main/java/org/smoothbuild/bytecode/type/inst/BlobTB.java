package org.smoothbuild.bytecode.type.inst;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.BLOB;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.hashed.Hash;

/**
 * This class is immutable.
 */
public class BlobTB extends BaseTB {
  public BlobTB(Hash hash) {
    super(hash, TypeNamesB.BLOB, BLOB);
  }

  @Override
  public BlobB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof BlobTB);
    return new BlobB(merkleRoot, bytecodeDb);
  }
}

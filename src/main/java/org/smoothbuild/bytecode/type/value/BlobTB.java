package org.smoothbuild.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.BLOB;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.value.BlobB;
import org.smoothbuild.bytecode.hashed.Hash;

/**
 * This class is immutable.
 */
public class BlobTB extends TypeB {
  public BlobTB(Hash hash) {
    super(hash, ValidNamesB.BLOB, BLOB);
  }

  @Override
  public BlobB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof BlobTB);
    return new BlobB(merkleRoot, bytecodeDb);
  }
}

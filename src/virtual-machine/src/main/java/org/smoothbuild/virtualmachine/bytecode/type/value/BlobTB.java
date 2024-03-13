package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;

/**
 * This class is immutable.
 */
public class BlobTB extends TypeB {
  public BlobTB(Hash hash) {
    super(hash, TypeNamesB.BLOB, BlobB.class);
  }

  @Override
  public BlobB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BlobTB);
    return new BlobB(merkleRoot, exprDb);
  }
}

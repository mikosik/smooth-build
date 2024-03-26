package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;

/**
 * This class is immutable.
 */
public class BBlobType extends BType {
  public BBlobType(Hash hash) {
    super(hash, BTypeNames.BLOB, BBlob.class);
  }

  @Override
  public BBlob newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BBlobType);
    return new BBlob(merkleRoot, exprDb);
  }
}

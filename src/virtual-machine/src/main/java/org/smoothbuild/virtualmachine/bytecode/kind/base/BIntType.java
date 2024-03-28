package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;

/**
 * This class is immutable.
 */
public final class BIntType extends BType {
  public BIntType(Hash hash) {
    super(hash, BTypeNames.INT, BInt.class);
  }

  @Override
  public BInt newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BIntType);
    return new BInt(merkleRoot, exprDb);
  }
}

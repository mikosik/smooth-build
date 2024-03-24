package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;

/**
 * This class is immutable.
 */
public class BIntType extends BType {
  public BIntType(Hash hash) {
    super(hash, BTypeNames.INT, BInt.class);
  }

  @Override
  public BInt newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BIntType);
    return new BInt(merkleRoot, exprDb);
  }
}

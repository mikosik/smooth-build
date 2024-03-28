package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;

/**
 * This class is immutable.
 */
public final class BStringType extends BType {
  public BStringType(Hash hash) {
    super(hash, BTypeNames.STRING, BString.class);
  }

  @Override
  public BString newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BStringType);
    return new BString(merkleRoot, exprDb);
  }
}

package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;

/**
 * This class is immutable.
 */
public class BStringType extends BType {
  public BStringType(Hash hash) {
    super(hash, BTypeNames.STRING, BString.class);
  }

  @Override
  public BString newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BStringType);
    return new BString(merkleRoot, exprDb);
  }
}

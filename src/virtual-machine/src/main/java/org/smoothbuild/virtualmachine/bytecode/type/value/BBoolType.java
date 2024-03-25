package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;

/**
 * This class is immutable.
 */
public class BBoolType extends BType {
  public BBoolType(Hash hash) {
    super(hash, BTypeNames.BOOL, BBool.class);
  }

  @Override
  public BBool newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BBoolType);
    return new BBool(merkleRoot, exprDb);
  }
}

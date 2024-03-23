package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BLambda;

public final class BLambdaCategory extends BFuncCategory {
  public BLambdaCategory(Hash hash, BFuncType funcType) {
    super(hash, "LAMBDA", funcType, BLambda.class);
  }

  @Override
  public BLambda newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BLambdaCategory);
    return new BLambda(merkleRoot, exprDb);
  }
}

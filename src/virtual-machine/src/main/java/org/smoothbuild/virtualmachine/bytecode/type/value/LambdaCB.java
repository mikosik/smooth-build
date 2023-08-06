package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.LAMBDA;

import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.LambdaB;

public final class LambdaCB extends FuncCB {
  public LambdaCB(Hash hash, FuncTB funcTB) {
    super(hash, LAMBDA, funcTB);
  }

  @Override
  public LambdaB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof LambdaCB);
    return new LambdaB(merkleRoot, exprDb);
  }
}
